var recordpath = getCookie("recordDataPath");
var datajspath = "<script language=javascript src='" + recordpath + "/data.js'></script>"
document.write(datajspath);
// document.write("<script language=javascript src='Gauss200 OLAP V100R007C10/zh/data.js'></script>");
var tbodyTdbId = document.getElementById("tbodyTdbId");
var nameS = undefined;
var tempClass = [];
var tempSuggestion = "";
var num = 0;
var record = 0;
var tempClassBool = true;
var bool = false;
var commandIdArr = [];
var commandName = [];
var allcmds = [];
var description = "";

function LoadXmlSuggestion(recordpath) {
    commands = new Commands();
    var xml = new ActiveXObject("Microsoft.XMLDOM");
    xml.async = false;
    xml.load(recordpath + "/suggestion.xml");
    commands.load(xml);
    allcmds = commands.cmds;
}
//获取功能描述的内容添加到说明对应的位置
function getSQLHtmlForDescription(tempValues, NameSnext) {
    $.each(tempValues, function(n, v) {
        if (v.alias == NameSnext || NameSnext == v.id) {
            $("#getdescriptioncontent").html(v.suggestion);
            description = $("div.section-item-content p:first").text();
        }
    });
}

function  GetChinese(strValue)  {    
    if (strValue !=  null  &&  strValue !=  "") {         
        var  reg  =  /[\u4e00-\u9fa5]/g;          
        return strValue.match(reg).join("");     
    }      
    else {
        return  "";  
    }          
} 

function responseButton() {
    //用heroers存储json文件里menbers的信息
    var heroes = db['sheet2'];
    for (var i = 0; i < heroes.length; i++) {
        var commandId = "commandId" + num;
        if (nameS == heroes[i].功能大类) {
            if (heroes[i].功能子类 != "") {
                var tbodySubClassId = "#" + heroes[i].功能子类;
                for (var j = 0; j <= tempClass.length; j++) {
                    if (tempClass[j] == heroes[i].功能子类) {
                        tempClassBool = false;
                    }
                }
                if (tempClassBool) {

                    tempClass[tempClass.length] = heroes[i].功能子类;
                    tempSuggestion = "<div><div class = 'font_color' style = 'margin: 10px 0px 10px 20px;'>" + heroes[i].功能子类 + "</div>" + "<table class = 'operShowTable'><thead class='tablehead'><tr><th class = 'title_task'>任务</th><th class = 'title_cmd'>命令</th><th>说明</th></tr></thead><tbody id = " + heroes[i].功能子类 + "></table></table></div>";
                    $('#haveSubclass').append(tempSuggestion);
                }
                tempClassBool = true;
                getSQLHtmlForDescription(allcmds, heroes[i].操作命令);
                tempSuggestion = "<tr><td class = 'font_color'><a class = 'font_color' href='enter_list_next.html?param1=" + record + "&param2=" + heroes[i].操作命令 + "'>" + heroes[i].任务名称 + "</a></td><td style = 'font-size: 11px;'><a class = 'font_color' id=" + commandId + " href='enter_list_next.html?param1=" + record + "&param2=" + heroes[i].操作命令 + "'>" + heroes[i].操作命令 + "</a></td><td>" + description + "</td></tr>";
                commandIdArr[commandIdArr.length] = commandId;
                num++;
                $(tbodySubClassId).append(tempSuggestion);
                $("#noSubclass").addClass("hidden");
                $("#haveSubclass").removeClass("hidden");
            } else {
                $("#noSubclass").removeClass("hidden");
                $("#haveSubclass").addClass("hidden");
                getSQLHtmlForDescription(allcmds, heroes[i].操作命令);
                tempSuggestion = "<tr><td class = 'font_color'><a class = 'font_color' href='enter_list_next.html?param1=" + record + "&param2=" + heroes[i].操作命令 + "'>" + heroes[i].任务名称 + "</a></td><td style = 'font-size: 11px;'><a class = 'font_color' id=" + commandId + " href='enter_list_next.html?param1=" + record + "&param2=" + heroes[i].操作命令 + "'>" + heroes[i].操作命令 + "</a></td><td>" + description + "</td></tr>"
                commandIdArr[commandIdArr.length] = commandId;
                num++;
                $('#tbodyTdbId').append(tempSuggestion);
            }


        } else if (nameS == heroes[i].大场景) {
            if (heroes[i].子场景 != "") {
                if (tempClass != heroes[i].子场景) {
                    tempSuggestion = "<div>" + heroes[i].子场景 + "<div>" + "<thead class='tablehead'><tr><th>任务</th><th>命令</th><th>说明</th></tr></thead>";
                    $('#haveSubclass').append(tempSuggestion);
                }
                getSQLHtmlForDescription(allcmds, heroes[i].操作命令);
                tempSuggestion = "<tr><td class = 'font_color'><a class = 'font_color' href='enter_list_next.html?param1=" + record + "&param2=" + heroes[i].操作命令 + "'>" + heroes[i].任务名称 + "</a></td><td style = 'font-size: 11px;'><a class = 'font_color' id=" + commandId + " href='enter_list_next.html?param1=" + record + "&param2=" + heroes[i].操作命令 + "'>" + heroes[i].操作命令 + "</a></td><td>" + description + "</td></tr>";
                commandIdArr[commandIdArr.length] = commandId;
                num++;
                $('#haveSubclass').append(tempSuggestion);
                $("#noSubclass").addClass("hidden");
                $("#haveSubclass").removeClass("hidden");
            } else {
                $("#noSubclass").removeClass("hidden");
                $("#haveSubclass").addClass("hidden");
                getSQLHtmlForDescription(allcmds, heroes[i].操作命令);
                tempSuggestion = "<tr><td class = 'font_color'><a class = 'font_color' href='enter_list_next.html?param1=" + record + "&param2=" + heroes[i].操作命令 + "'>" + heroes[i].任务名称 + "</a></td><td style = 'font-size: 11px;'><a class = 'font_color' id=" + commandId + " href='enter_list_next.html?param1=" + record + "&param2=" + heroes[i].操作命令 + "'>" + heroes[i].操作命令 + "</a></td><td>" + description + "</td></tr>"
                commandIdArr[commandIdArr.length] = commandId;
                num++;
                $('#tbodyTdbId').append(tempSuggestion);

            }
        } else if (nameS == heroes[i].操作类别1) {
            getSQLHtmlForDescription(allcmds, heroes[i].操作命令);
            tempSuggestion = "<tr><td class = 'font_color'><a class = 'font_color' href='enter_list_next.html?param1=" + record + "&param2=" + heroes[i].操作命令 + "'>" + heroes[i].任务名称 + "</a></td><td style = 'font-size: 11px;'><a class = 'font_color' id=" + commandId + " href='enter_list_next.html?param1=" + record + "&param2=" + heroes[i].操作命令 + "'>" + heroes[i].操作命令 + "</a></td><td>" + description + "</td></tr>"
            commandIdArr[commandIdArr.length] = commandId;
            num++;
            $('#tbodyTdbId').append(tempSuggestion);

        } else if (nameS == heroes[i].操作类别2) {
            getSQLHtmlForDescription(allcmds, heroes[i].操作命令);
            tempSuggestion = "<tr><td class = 'font_color'><a class = 'font_color' href='enter_list_next.html?param1=" + record + "&param2=" + heroes[i].操作命令 + "'>" + heroes[i].任务名称 + "</a></td><td style = 'font-size: 11px;'><a class = 'font_color' id=" + commandId + " href='enter_list_next.html?param1=" + record + "&param2=" + heroes[i].操作命令 + "'>" + heroes[i].操作命令 + "</a></td><td>" + description + "</td></tr>"
            commandIdArr[commandIdArr.length] = commandId;
            num++;
            $('#tbodyTdbId').append(tempSuggestion);

        } else if (nameS == heroes[i].操作类别3) {
            getSQLHtmlForDescription(allcmds, heroes[i].操作命令);
            tempSuggestion = "<tr><td class = 'font_color'><a class = 'font_color' href='enter_list_next.html?param1=" + record + "&param2=" + heroes[i].操作命令 + "'>" + heroes[i].任务名称 + "</a></td><td style = 'font-size: 11px;'><a class = 'font_color' id=" + commandId + " href='enter_list_next.html?param1=" + record + "&param2=" + heroes[i].操作命令 + "'>" + heroes[i].操作命令 + "</a></td><td>" + description + "</td></tr>"
            commandIdArr[commandIdArr.length] = commandId;
            num++;
            $('#tbodyTdbId').append(tempSuggestion);

        }
    }
}
//读取cookies 
function getCookie(name) {
    var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");

    if (arr = document.cookie.match(reg))

        return unescape(arr[2]);
    else
        return null;
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
    bool = getParams("param3");
    if (bool) {
        record = getParams("param4");
    } else {
        record = getParams("param");
    }
    nameS = heroes[record].分类名称;
    $("#operNameS").text(nameS);
    $("#operNameB").html(function() {
        var operNameContentB = "<b>" + nameS + "</b>"
        return operNameContentB;
    });
    LoadXmlSuggestion(recordpath);
    responseButton();

});