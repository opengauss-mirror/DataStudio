/*
 * Copyright (c) 2026 Huawei Technologies Co., Ltd.
 * DataStudio is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

function ErrorCodes()
{
	this.errs=[];
	this.add=add;
	this.find=find;
	this.get=get;
	this.load=load;
	var __that__=this;
	function add(err)
	{
    this.errs[this.errs.length]=err;
	}
	function find(errId)
	{
		var tmpErr="";
		$(this.errs).each(function(i,err)
		{
			if(errId==err.id)
			{
				tmpErr=err;
			}
		});
		return tmpErr;
	}
	function get(index)
	{
		return this.errs[index];
	}
	function load(xml)
	{
		__that__.errs.length=0;
		$(xml).find("errorcode").each(function(i,errXml)
		{
      var err=new ErrorCode(errXml.getAttribute("id"),errXml.childNodes[0].text,errXml.childNodes[1].text,errXml.childNodes[2].text,errXml.childNodes[3].text);
			__that__.add(err);					
		});
	}
}

function ErrorCode(id,description,sqlstate,cause,solution)
{
	this.id=id;
	this.description=description;
	this.sqlstate=sqlstate;
	this.cause=cause;
	this.solution=solution;
}