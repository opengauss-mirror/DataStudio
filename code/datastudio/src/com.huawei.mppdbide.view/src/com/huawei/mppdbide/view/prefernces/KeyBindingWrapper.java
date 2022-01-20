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

package com.huawei.mppdbide.view.prefernces;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.bindings.EBindingService;
import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.preference.PreferenceStore;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class KeyBindingWrapper.
 *
 * @since 3.0.0
 */
public final class KeyBindingWrapper {
    private static volatile KeyBindingWrapper wrapper;
    private EBindingService service;
    private ECommandService commandService;

    /**
     * The Constant KEYBINDING.
     */
    static final String KEYBINDING = "keybinding";

    /**
     * The Constant COMMANDID.
     */
    static final String COMMANDID = "commandid";

    /**
     * The Constant COMMANDNAME.
     */
    static final String COMMANDNAME = "commandname";

    /**
     * The Constant COMMAND.
     */
    static final String COMMAND = "command";

    /**
     * The Constant DESCRIPTION.
     */
    static final String DESCRIPTION = "description";

    /**
     * The Constant DEFAULTKEY.
     */
    static final String DEFAULTKEY = "defaultkey";

    private List<KeyBinding> list;
    private HashMap<String, KeyBinding> keyBindMap;
    private static final String AUTOSUGGESTCOMMANDID = "Auto Suggest";
    private static final String CODETEMLPLATECOMMANDID = "com.huawei.mppdbide.editor.shortcutmapper.codetemplate";
    private static String lastBindSchemaId;
    private static String lastBindContextId;
    private static String lastBindLocale;
    private static String lastBindPlatform;
    private static int lastBindType;
    private static final Object LOCK = new Object();

    /**
     * Instantiates a new key binding wrapper.
     *
     * @param bindService the bind service
     * @param cmdSrvce the cmd srvce
     */
    private KeyBindingWrapper(EBindingService bindService, ECommandService cmdSrvce) {
        service = bindService;
        commandService = cmdSrvce;
        keyBindMap = new HashMap<String, KeyBinding>(20);
        list = new ArrayList<KeyBinding>(5);
    }

    /**
     * Gets the single instance of KeyBindingWrapper.
     *
     * @param bindService the bind service
     * @param cmdSrvce the cmd srvce
     * @return single instance of KeyBindingWrapper
     */
    public static KeyBindingWrapper getInstance(EBindingService bindService, ECommandService cmdSrvce) {
        if (null == wrapper) {
            synchronized (LOCK) {
                if (null == wrapper) {
                    wrapper = new KeyBindingWrapper(bindService, cmdSrvce);
                }
            }
        }
        return wrapper;
    }

    /**
     * Gets the single instance of KeyBindingWrapper.
     *
     * @return single instance of KeyBindingWrapper
     */
    public static KeyBindingWrapper getInstance() {
        // No check any time.
        return wrapper;
    }

    /**
     * Reconfigure key binding.
     *
     * @param ps the ps
     */
    public void reconfigureKeyBinding(PreferenceStore ps) {
        List<KeyBinding> bindingList = getList();
        for (KeyBinding k : bindingList) {
            String newKeyStroke = ps.getString(k.getCommand());

            k.setNewKey(newKeyStroke);
            TriggerSequence newTrigSeq = service.createSequence(newKeyStroke);
            if (newTrigSeq == null) {
                PreferenceWrapper.getInstance().setPreferenceValid(true);
                k.setNewKey(k.getDefaultKey());
                ps.setValue(k.getCommand(), k.getNewKey());
            }
            configureKey(k);
        }
        clearLastKeyBindingsCache();

    }

    /**
     * Configure key.
     *
     * @param key the key
     */
    public void configureKey(KeyBinding key) {
        String commandStrng = key.getCommandId();
        commandStrng = commandStrng.trim();
        Command command = commandService.getCommand(commandStrng);
        ParameterizedCommand parameterizedCommand;
        parameterizedCommand = addParametersToCommand(commandStrng, command);

        Collection<Binding> binds = getBindings(parameterizedCommand);
        if (null != binds && 0 < binds.size()) {
            Iterator<Binding> it = binds.iterator();
            boolean hasNextRec = it.hasNext();
            Binding bind = null;
            while (hasNextRec) {
                bind = (Binding) it.next();
                cacheLastKeyBindings(bind);
                service.deactivateBinding(bind);
                activateBindingFor(parameterizedCommand, bind, key);

                hasNextRec = it.hasNext();
            }
        } else {
            activateBindingFor(parameterizedCommand, null, key);

        }

    }

    private ParameterizedCommand addParametersToCommand(String commandStrng, Command command) {
        if (commandStrng.equals("com.huawei.mppdbide.command.id.executeobjectbrowseritemfromtoolbarnewtab")) {
            Map<String, String> parameters = new HashMap<String, String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
            parameters.put("new.tab", "truekey");
            return ParameterizedCommand.generateCommand(command, parameters);
        } else {
            return new ParameterizedCommand(command, null);
        }
    }

    /**
     * Activate binding for.
     *
     * @param parameterizedCommand the parameterized command
     * @param bind the bind
     * @param key the key
     */
    private void activateBindingFor(ParameterizedCommand parameterizedCommand, Binding bind, KeyBinding key) {
        // if empty, un-bind key
        if (!key.getNewKey().isEmpty()) {
            final TriggerSequence newTrigSeq = service.createSequence(key.getNewKey());
            Binding newBind = null;
            if (null != bind) {
                newBind = new CusomBinding(parameterizedCommand, bind.getSchemeId(), bind.getContextId(),
                        bind.getLocale(), bind.getPlatform(), null, bind.getType(), newTrigSeq);
            } else {
                newBind = new CusomBinding(parameterizedCommand, lastBindSchemaId, lastBindContextId, lastBindLocale,
                        lastBindPlatform, null, lastBindType, newTrigSeq);
            }
            service.activateBinding(newBind);

            resolveConflicts(newBind, key.getCommandId());

        }
    }

    /**
     * Resolve conflicts.
     *
     * @param newBind the new bind
     * @param cmdName the cmd name
     */
    private void resolveConflicts(Binding newBind, String cmdName) {
        Collection<Binding> binds = service.getConflictsFor(newBind.getTriggerSequence());
        if (null != binds) {
            for (Binding bind : binds) {
                if (!newBind.equals(bind)) {
                    service.deactivateBinding(bind);

                    String commandId = bind.getParameterizedCommand().getCommand().getId();
                    if (keyBindMap.containsKey(commandId)) {
                        keyBindMap.get(commandId).setNewKey("");
                    }
                }
            }

        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class CusomBinding.
     */
    private static final class CusomBinding extends Binding {
        private TriggerSequence newTrigSeq;

        /**
         * Instantiates a new cusom binding.
         *
         * @param command the command
         * @param schemeId the scheme id
         * @param contextId the context id
         * @param locale the locale
         * @param platform the platform
         * @param windowManager the window manager
         * @param type the type
         * @param newTrigSeq the new trig seq
         */
        protected CusomBinding(ParameterizedCommand command, String schemeId, String contextId, String locale,
                String platform, String windowManager, int type, TriggerSequence newTrigSeq) {
            super(command, schemeId, contextId, locale, platform, windowManager, type);
            this.newTrigSeq = newTrigSeq;
        }

        @Override
        public TriggerSequence getTriggerSequence() {
            return this.newTrigSeq;
        }

    }

    /**
     * Gets the bindings.
     *
     * @param parameterizedCommand the parameterized command
     * @return the bindings
     */
    private Collection<Binding> getBindings(ParameterizedCommand parameterizedCommand) {
        Collection<Binding> binds = service.getBindingsFor(parameterizedCommand);
        if (0 == binds.size()) {
            Collection<Binding> conflicts = service.getAllConflicts() == null ? new ArrayList<Binding>()
                    : service.getAllConflicts();
            for (Binding bind : conflicts) {
                if (bind.getParameterizedCommand().equals(parameterizedCommand)) {
                    service.deactivateBinding(bind);
                }
            }

            binds = service.getBindingsFor(parameterizedCommand);
        }

        return binds;
    }

    /**
     * Cache last key bindings.
     *
     * @param bind the bind
     */
    private static void cacheLastKeyBindings(Binding bind) {
        if (null == lastBindSchemaId) {
            lastBindSchemaId = bind.getSchemeId();
            lastBindContextId = bind.getContextId();
            lastBindLocale = bind.getLocale();
            lastBindPlatform = bind.getPlatform();
            lastBindType = bind.getType();
        }
    }

    /**
     * Clear last key bindings cache.
     */
    private static void clearLastKeyBindingsCache() {
        lastBindSchemaId = null;
        lastBindContextId = null;
        lastBindLocale = null;
        lastBindPlatform = null;
        lastBindType = 0;
    }

    /**
     * Load XML file.
     *
     * @param configFile the config file
     * @return the list
     */
    public List<KeyBinding> loadXMLFile(URL configFile) {
        InputStream in = null;
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            in = configFile.openStream();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

            KeyBinding binding = null;

            while (eventReader != null && eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have an item element, we create a new item
                    if (KEYBINDING.equals(startElement.getName().getLocalPart())) {
                        binding = new KeyBinding();
                    }

                    event = writeXmlDataToBinding(eventReader, binding, event);
                }
                addBindingData(binding, event);
            }

        } catch (IOException e) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.PREF_FILE_ERR_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.PREF_FILE_ERR_MSG));
        } catch (XMLStreamException e) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.PREF_FILE_ERR_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.PREF_FILE_ERR_MSG));

        } finally {
            closeInputStream(in);
        }

        return list;
    }

    /**
     * Write xml data to binding.
     *
     * @param eventReader the event reader
     * @param binding the binding
     * @param eventParam the event param
     * @return the XML event
     * @throws XMLStreamException the XML stream exception
     */
    private XMLEvent writeXmlDataToBinding(XMLEventReader eventReader, KeyBinding binding, XMLEvent eventParam)
            throws XMLStreamException {
        XMLEvent event = eventParam;
        if (null != binding) {
            switch (event.asStartElement().getName().getLocalPart()) {
                case COMMANDID: {
                    event = eventReader.nextEvent();
                    binding.setCommandId(event.asCharacters().getData().trim());
                    return event;
                }
                case COMMANDNAME: {
                    event = eventReader.nextEvent();
                    String getName = event.asCharacters().getData();
                    binding.setCommandName(MessageConfigLoader.getProperty(getName));
                    return event;
                }
                case COMMAND: {
                    event = eventReader.nextEvent();
                    binding.setCommand(event.asCharacters().getData());
                    return event;
                }
                case DESCRIPTION: {
                    event = eventReader.nextEvent();
                    binding.setDescription(event.asCharacters().getData());
                    return event;
                }
                case DEFAULTKEY: {
                    event = eventReader.nextEvent();
                    binding.setDefaultKey(event.asCharacters().getData());
                    return event;
                }
                default: {
                    break;
                }
            }

        }
        return event;
    }

    /**
     * Adds the binding data.
     *
     * @param binding the binding
     * @param event the event
     */
    private void addBindingData(KeyBinding binding, XMLEvent event) {
        if (event.isEndElement()) {
            EndElement endElement = event.asEndElement();
            if (endElement.getName().getLocalPart().equals(KEYBINDING)) {
                if (null != binding) {
                    keyBindMap.put(binding.getCommandId(), binding);
                    list.add(binding);
                }

            }
        }
    }

    /**
     * Close input stream.
     *
     * @param in the in
     */
    private void closeInputStream(InputStream in) {
        if (null != in) {
            try {
                in.close();
            } catch (IOException exception) {
                MPPDBIDELoggerUtility.error("KeyBindingWrapper: closing InputStream failed.", exception);
            }
        }
    }

    /**
     * Sets the default key mapper preferences.
     *
     * @param preferenceStore the new default key mapper preferences
     */
    public void setDefaultKeyMapperPreferences(PreferenceStore preferenceStore) {
        for (KeyBinding key : list) {

            preferenceStore.setDefault(key.getCommand(), key.getDefaultKey());
        }

    }

    /**
     * Gets the list.
     *
     * @return the list
     */
    public List<KeyBinding> getList() {
        return list;
    }

    /**
     * Gets the auto sugest key.
     *
     * @return the auto sugest key
     */
    public String getAutoSugestKey() {
        return keyBindMap.get(KeyBindingWrapper.AUTOSUGGESTCOMMANDID).getNewKey();
    }

    /**
     * Gets the code template key.
     *
     * @return the code template key
     */
    public String getCodeTemplateKey() {
        return keyBindMap.get(KeyBindingWrapper.CODETEMLPLATECOMMANDID).getNewKey();
    }
}
