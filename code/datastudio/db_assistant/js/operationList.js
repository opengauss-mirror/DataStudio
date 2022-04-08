var recordpath = getCookie("recordDataPath");
var datajspath = "<script language=javascript src='" + recordpath + "/data.js'></script>"
document.write(datajspath);
var operNameS = document.getElementById("operNameS");
var operNameB = document.getElementById("operNameB");

function createButton(data) {
    //用heroers存储json文件里menbers的信息
    var heroes = data['sheet1'];
    var notscenDimen = true;
    var notoperDimen = true;
    for (var i = 0; i < heroes.length; i++) {
        var buttonId = "button" + i;
        switch (heroes[i].分类维度) {
            case "功能大类":
                {
                    $('.functionalDimension').removeClass('hidden');
                    myArticle = "<a class='button-item-A' href='enter_list.html?param=" + i + "'><div class ='button-item center' id=" + buttonId + ">" + heroes[i].分类名称 + "</div></a>"
                    $("#funcDimen").append(myArticle);
                };
                break;
            case "大场景":
                {
                    notscenDimen = false;
                    $('.sceneDimension').removeClass('hidden');
                    myArticle = "<a class='button-item-A' href='enter_list.html?param=" + i + "'><div class ='button-item center' id=" + buttonId + ">" + heroes[i].分类名称 + "</div></a>"
                    $("#scenDimen").append(myArticle);
                };
                break;
            case "操作类别":
                {
                    notoperDimen = false;
                    $('.operationalDimension').removeClass('hidden');
                    myArticle = "<a class='button-item-A' href='enter_list.html?param=" + i + "'><div class ='button-item center' id=" + buttonId + ">" + heroes[i].分类名称 + "</div></a>"
                    $("#operDimen").append(myArticle);
                };
            default:
                break;
        }
    }
    if (notoperDimen && notscenDimen) {
        $('.is-func-deime').addClass('hidden');
    }
}
var mun = 0;

//读取cookies 
function getCookie(name) {
    var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");

    if (arr = document.cookie.match(reg))

        return unescape(arr[2]);
    else
        return null;
}


$(function() {
    // $("#operNameC").append(recordpath);
    createButton(db);

});