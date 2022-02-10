/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles.ImportConnectionProfileCore.MatchedConnectionProfiles;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportConnectionProfileManager.
 *
 * @since 3.0.0
 */
public class ImportConnectionProfileManager {

    List<IServerConnectionInfo> copyList;
    Map<String, MatchedConnectionProfiles> replaceMap;
    private ImportConnectionProfileCore core;

    /**
     * Instantiates a new import connection profile manager.
     *
     * @param core the core
     */
    public ImportConnectionProfileManager(ImportConnectionProfileCore core) {
        this.core = core;
        copyList = new LinkedList<>();
        replaceMap = new HashMap<String, MatchedConnectionProfiles>();
    }

    /**
     * Merge all profiles.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    public void mergeAllProfiles() throws DatabaseOperationException {

        List<IServerConnectionInfo> allProfiles = core.getOriginalDestinationList();
        List<IServerConnectionInfo> importedProfilesList = core.getUniqueList();
        List<IServerConnectionInfo> finalListOfProfiles = new LinkedList<IServerConnectionInfo>();
        try {
            int maxProfileId = Integer.parseInt(calculateMaxProfileValue(allProfiles));
            int maxID = maxProfileId;
            for (IServerConnectionInfo info : importedProfilesList) {
                ++maxID;
                info.setProfileId("" + maxID);
                finalListOfProfiles.add(info);

            }

            ConnectionProfileManagerImpl instance = ConnectionProfileManagerImpl.getInstance();
            if (!finalListOfProfiles.isEmpty()) {
                instance.mergeImportedProfiles(finalListOfProfiles, allProfiles);
            }
            if (!copyList.isEmpty()) {
                updateCopyList(allProfiles);
                instance.mergeImportedProfiles(copyList, allProfiles);
            }

            if (!replaceMap.isEmpty()) {
                Iterator<Entry<String, MatchedConnectionProfiles>> iterator = replaceMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, MatchedConnectionProfiles> next = iterator.next();
                    updateReplaceProfilesList(allProfiles, next);
                    instance.replaceWithImportedProfiles(next.getValue().getDestProfile(),
                            next.getValue().getSourceProfile(), allProfiles);
                }
            }
        } catch (NumberFormatException exp) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERROR_IMPORTING_CONNECTION_PROFILES), exp);
            throw new DatabaseOperationException(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERROR_IMPORTING_CONNECTION_PROFILES));
        } catch (DataStudioSecurityException exp) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERROR_IMPORTING_CONNECTION_PROFILES), exp);
            throw new DatabaseOperationException(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERROR_IMPORTING_CONNECTION_PROFILES));
        }

    }

    private void updateReplaceProfilesList(List<IServerConnectionInfo> allProfiles,
            Entry<String, MatchedConnectionProfiles> entry) {
        MatchedConnectionProfiles value = entry.getValue();
        String profileId = value.getDestProfile().getProfileId();
        String profileId2 = value.getSourceProfile().getProfileId();
        if (!profileId.equals(profileId2)) {
            IServerConnectionInfo serverConnectionInfo = value.getSourceProfile();
            int updateProfileID = findLeastAvailableProFileID(allProfiles);
            serverConnectionInfo.setProfileId("" + updateProfileID);
        }
    }

    private int findLeastAvailableProFileID(List<IServerConnectionInfo> allProfiles) {
        List<Integer> originalPorfileIDList = allProfiles.stream().map(entry -> Integer.parseInt(entry.getProfileId()))
                .collect(Collectors.toList());

        return evaluateMinValue(originalPorfileIDList);
    }

    private int evaluateMinValue(List<Integer> profiles) {
        int minValue = 0;
        Collections.sort(profiles);
        for (Integer val : profiles) {
            ++minValue;
            if (val != minValue) {
                break;
            }

        }

        if (minValue == 0 || minValue == profiles.get(minValue - 1)) {
            ++minValue;
        }

        return minValue;
    }

    private void updateCopyList(List<IServerConnectionInfo> allProfiles) {
        int maxProfileID = Integer.parseInt(calculateMaxProfileValue(allProfiles));
        for (IServerConnectionInfo info : copyList) {
            ++maxProfileID;
            info.setProfileId("" + maxProfileID);

            handleConnectionName(allProfiles, info);
            allProfiles.add(info);
        }
    }

    private void handleConnectionName(List<IServerConnectionInfo> allProfiles, IServerConnectionInfo info) {
        String copyString = info.getConectionName();
        copyString = copyString.replaceAll("\\((\\d+)\\)", "");
        Pattern pattern = Pattern.compile(Pattern.quote(copyString) + "\\((\\d+)\\)");
        Matcher matcher = null;
        List<Integer> collect = new ArrayList<>();
        for (IServerConnectionInfo prof : allProfiles) {
            matcher = pattern.matcher(prof.getConectionName());
            if (matcher.matches() && !matcher.group(1).isEmpty()) {
                collect.add(Integer.parseInt(matcher.group(1)));
            }
        }
        int minValue = evaluateMinValue(collect);

        info.setConectionName(copyString + "(" + minValue + ")");
    }

    private String calculateMaxProfileValue(List<IServerConnectionInfo> profilesList) {
        if (profilesList.isEmpty()) {
            return "" + 0;
        }
        List<Integer> collect = profilesList.stream().map(entry -> Integer.parseInt(entry.getProfileId()))
                .collect(Collectors.toList());
        Integer integer = collect.stream().max(Integer::compare).get();
        return "" + integer;
    }

    /**
     * Adds the profiles to be overriden.
     *
     * @param matchedProfiles the matched profiles
     * @param option the option
     */
    public void addProfilesToBeOverriden(MatchedConnectionProfiles matchedProfiles, OverridingOptions option) {
        if (option == OverridingOptions.COPYANDKEEPBOTH) {
            copyList.add(matchedProfiles.getSourceProfile());
        } else if (option == OverridingOptions.REPLACE) {
            String sourceProfconectionName = matchedProfiles.getSourceProfile().getConectionName();
            if (!replaceMap.containsKey(sourceProfconectionName)) {
                replaceMap.put(sourceProfconectionName, matchedProfiles);
            }

        } else {
            return;
        }

    }

    /**
     * Handle all profiles with conflicts.
     *
     * @param option the option
     */
    public void handleAllProfilesWithConflicts(OverridingOptions option) {
        if (OverridingOptions.COPYANDKEEPBOTH == option) {
            List<MatchedConnectionProfiles> matchedList = core.getMatchedProfilesList();
            for (MatchedConnectionProfiles matchedProfile : matchedList) {
                if (!copyList.contains(matchedProfile.getSourceProfile())
                        && !replaceMap.containsKey(matchedProfile.getSourceProfile().getConectionName())) {
                    copyList.add(matchedProfile.getSourceProfile());
                }
            }
        } else if (OverridingOptions.REPLACE == option) {
            List<MatchedConnectionProfiles> matchedProfilesList = core.getMatchedProfilesList();
            for (MatchedConnectionProfiles matchedProfiles : matchedProfilesList) {

                String conectionName = matchedProfiles.getSourceProfile().getConectionName();
                if (!copyList.contains(matchedProfiles.getSourceProfile()) && !replaceMap.containsKey(conectionName)) {
                    replaceMap.put(conectionName, matchedProfiles);
                }
            }
        } else {
            return;
        }
    }

}
