var recordpath = getCookie("recordDataPath");
var datajspath = "<script language=javascript src='" + recordpath + "/data.js'></script>"
document.write(datajspath);
//document.write("<script language=javascript src='Gauss200 OLAP V100R007C10/zh/data.js'></script>");
var isNotSingle = true;
var selectedCmdAlias = "temp";
var formerCmd = "";
var cur_cmd = "";
var NameSnext;
//读取cookies 
function getCookie(name) {
    var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");

    if (arr = document.cookie.match(reg))

        return unescape(arr[2]);
    else
        return null;
}

function isHidden(oDiv) {
    var vDiv = oDiv.nextSibling;
    while (vDiv.nodeType == 3) {
        vDiv = vDiv.nextSibling;
    }
    vDiv.style.display = (vDiv.style.display == 'none') ? 'block' : 'none';
    var pngs = oDiv.getElementsByTagName("img");
    switch (pngs[1].style.display) {
        case 'none':
            pngs[0].style.display = 'none';
            pngs[1].style.display = 'inline';
            break;
        case 'inline':
            pngs[0].style.display = 'inline';
            pngs[1].style.display = 'none';
            break;
    }
}

function LoadSuggestion(recordpath) {
    commands = new Commands();
    var xml = new ActiveXObject("Microsoft.XMLDOM");
    xml.async = false;
    xml.load(recordpath + "/suggestion.xml");
    commands.load(xml);
    getSQLHtmlForConent(commands.cmds);

}



function getSQLHtmlForConent(tempValues) {

    $.each(tempValues, function(n, v) {
        if (v.alias == NameSnext || NameSnext == v.id) {
            processInput(NameSnext);
        }
    });
}

function processInput(tempId) {
    tempId = $.trim(tempId); //trim（）方法从字符串的两边删除空格
    if ("" != tempId) {
        var tempIds = tempId.split(/;/); //split（）方法用于将字符串拆分为子字符串数组，并返回新数组
        tempId = tempIds[tempIds.length - 1];
        if ("" == tempId || "$$" == tempId) tempId = tempIds[tempIds.length - 2];
        var tokens = StringReader(tempId);
        var tempCmds = [];
        var cur_pos = 0;
        for (var li = 0; li < tokens.length; li++) {
            if (tempCmds.length == 0) {
                tempCmds = commands.find(tokens[li].toUpperCase(), cur_pos); //toUpperCase() 方法用于把字符串转换为大写
                if (tempCmds.length != 0) {
                    cur_pos++;
                }
            } else {
                var formmerA = tempCmds;
                tempCmds = commands.findInArray(tempCmds, tokens[li].toUpperCase(), cur_pos);
                if (tempCmds.length != 0 && formmerA != tempCmds) {
                    cur_pos++;
                }
            }
        }
        if (tempCmds.length > 0) {
            var tempSuggestion = "";
            if (tempCmds.length == 1) {
                cur_cmd = tempCmds[0];
                if (cur_cmd.suggestion != "") {
                    tempSuggestion = "<h4 class='sectionCmdTitle'>" + cur_cmd.alias + "</h4>" + cur_cmd.suggestion + tempSuggestion;
                    if (cur_cmd.subs.length > 0) {
                        var tempSubSuggestion = "";
                        $(cur_cmd.subs).each(function(i, sub) {
                            {
                                var tempAllKeywords = ".*?" + ((sub.id).replace(/\s/g, ".*?"));
                                var reg = new RegExp(tempAllKeywords, 'gi');
                                if (reg.test(tempId) && "" != sub.suggestion) {
                                    tempSubSuggestion = sub.suggestion + tempSubSuggestion;
                                }
                            }
                        });
                        if (tempSubSuggestion != "")
                            tempSuggestion = "<p class=\"sectionSugTitle\">Suggestion</p>" + tempSubSuggestion + "<hr class='hrStyle'>" + tempSuggestion;
                    }
                }
            } else {
                cur_cmd = commands.getOneFromArray(tempCmds, cur_pos);

                if (cur_cmd != "") {
                    tempSuggestion = "<h4 class='sectionCmdTitle'>" + cur_cmd.alias + "</h4>" + cur_cmd.suggestion + tempSuggestion;
                    if (cur_cmd.subs.length > 0) {
                        var tempSubSuggestion = "";
                        $(cur_cmd.subs).each(function(i, sub) {
                            {
                                var tempAllKeywords = ".*?" + ((sub.id).replace(/\s/g, ".*?"));
                                var reg = new RegExp(tempAllKeywords, 'gi');
                                if (reg.test(tempId) && "" != sub.suggestion) {
                                    tempSubSuggestion = sub.suggestion + tempSubSuggestion;
                                }
                            }
                        });
                        if (tempSubSuggestion != "")
                            tempSuggestion = "<p class=\"sectionSugTitle\">Suggestion</p>" + tempSubSuggestion + "<hr class='hrStyle'>" + tempSuggestion;
                    }
                } else {
                    backString = tempId;
                    var num = 0;
                    $.each(tempCmds, function(n, v) {
                        cur_cmd = tempCmds[num];
                        if (v.alias.indexOf('|') > -1) {
                            if (v.alias == NameSnext) {
                                tempSuggestion = "<h4 class='sectionCmdTitle'>" + cur_cmd.alias + "</h4>" + cur_cmd.suggestion + tempSuggestion;
                                if (cur_cmd.subs.length > 0) {
                                    var tempSubSuggestion = "";
                                    $(cur_cmd.subs).each(function(i, sub) {
                                        {
                                            var tempAllKeywords = ".*?" + ((sub.id).replace(/\s/g, ".*?"));
                                            var reg = new RegExp(tempAllKeywords, 'gi');
                                            if (reg.test(tempId) && "" != sub.suggestion) {
                                                tempSubSuggestion = sub.suggestion + tempSubSuggestion;
                                            }
                                        }
                                    });
                                    if (tempSubSuggestion != "")
                                        tempSuggestion = "<p class=\"sectionSugTitle\">Suggestion</p>" + tempSubSuggestion + "<hr class='hrStyle'>" + tempSuggestion;
                                }
                            } else {
                                num++;
                            }
                        } else {
                            if (v.alias == NameSnext) {
                                tempSuggestion = "<h4 class='sectionCmdTitle'>" + cur_cmd.alias + "</h4>" + cur_cmd.suggestion + tempSuggestion;
                                if (cur_cmd.subs.length > 0) {
                                    var tempSubSuggestion = "";
                                    $(cur_cmd.subs).each(function(i, sub) {
                                        {
                                            var tempAllKeywords = ".*?" + ((sub.id).replace(/\s/g, ".*?"));
                                            var reg = new RegExp(tempAllKeywords, 'gi');
                                            if (reg.test(tempId) && "" != sub.suggestion) {
                                                tempSubSuggestion = sub.suggestion + tempSubSuggestion;
                                            }
                                        }
                                    });
                                    if (tempSubSuggestion != "")
                                        tempSuggestion = "<p class=\"sectionSugTitle\">Suggestion</p>" + tempSubSuggestion + "<hr class='hrStyle'>" + tempSuggestion;
                                }
                            } else {
                                num++;
                            }
                        }
                    });
                    formerCmd = "";
                }
            }
            if (cur_cmd != "" && formerCmd == "") {
                $('#divSuggestion').html(tempSuggestion);
                $(document).scrollTop(0);
                assistantHighlight("assistantExamples");
                formerCmd = cur_cmd;
            } else if (cur_cmd != "" && formerCmd != "" && cur_cmd.alias != formerCmd.alias) {
                $('#divSuggestion').html(tempSuggestion);
                $(document).scrollTop(0);
                assistantHighlight("assistantExamples");
                formerCmd = cur_cmd;
            } else if (formerCmd == "") {
                $('#divSuggestion').html(tempSuggestion);
                $(document).scrollTop(0);
            }
        }
    }
}

function getParams(key) {
    var reg = new RegExp("(^|&)" + key + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return unescape(r[2]);
    }
    return null;
};
$(function() {
    $("#grammarNext").click(function() {
        location.href = "index_zh.html";
        $(this).addClass("active");
        $("#operationListNext").removeClass("active");
        $("#beginnerGuideNext").removeClass("active");
        $("#caseNext").removeClass("active");
    });
    $("#beginnerGuideNext").click(function() {
        $(this).addClass("active");
        $("#operationListNext").removeClass("active");
        $("#grammarNext").removeClass("active");
        $("#caseNext").removeClass("active");
    });

    $("#caseNext").click(function() {
        $(this).addClass("active");
        $("#operationListNext").removeClass("active");
        $("#beginnerGuideNext").removeClass("active");
        $("#grammarNext").removeClass("active");
    });
    var heroes = db['sheet1'];
    var mun = getParams("param1");
    NameS = heroes[mun].分类名称;
    NameSnext = getParams("param2");
    $("#operNameSnext").text(NameSnext);
    var operNameNext = "<a class='button-item-A' href='enter_list.html?param3=true&param4=" + mun + "'>" + NameS + "</a>"
    $("#operNameS").append(operNameNext);
    // $("#operNameB").append(recordpath);
    LoadSuggestion(recordpath);

});