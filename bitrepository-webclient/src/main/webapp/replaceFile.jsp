<%--
  #%L
  Bitrepository Webclient
  %%
  Copyright (C) 2010 - 2012 The State and University Library, The Royal Library and The State Archives, Denmark
  %%
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as 
  published by the Free Software Foundation, either version 2.1 of the 
  License, or (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Lesser Public License for more details.
  
  You should have received a copy of the GNU General Lesser Public 
  License along with this program.  If not, see
  <http://www.gnu.org/licenses/lgpl-2.1.html>.
  #L%
  --%>

<%@page import="org.bitrepository.webservice.ServiceUrlFactory" %>
<%@page import="org.bitrepository.webservice.ServiceUrl" %>
<html>
    <link type="text/css" href="css/ui-lightness/jquery-ui-1.8.16.custom.css" rel="Stylesheet" />	
    <script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
    <script type="text/javascript" src="defaultText.js"></script>

    <script>
        $(function() {
            $("#replaceForm").buttonset();
        });
    </script>

    <div class=ui-widget>
        <form id="replaceForm" action="javascript:submit()">
            <p><b>Replace file</b></p>
            <table border="0">
                <tr>
                    <td>File ID:</td>
                    <td>&nbsp;</td>
                    <td><input class="defaultText" title="File ID" id="fileID" type="text"/></td>
                    <td>&nbsp;&nbsp;</td>
                    <td>Pillar ID:</td>
                    <td>&nbsp;</td>
                    <td>
                    	<select id="pillarSelector">
                    		<option value=""> </option>
                		</select>	
                    </td>
                </tr>
                <tr>
                    <td>Fileaddress:</td>
                    <td>&nbsp;</td>
                    <% ServiceUrl su = ServiceUrlFactory.getInstance(); %>
                    <td> <input type="text" id="fileaddr" class="inputURL" value="<%= su.getDefaultHttpServerUrl() %>"/></td>
                    <td>&nbsp;&nbsp;</td>
                    <td>Filesize:</td>
                    <td>&nbsp;</td>
                    <td><input class="defaultText" title="Filesize" id="filesize" type="text"/></td>
                </tr>
                <tr>
                    <td>Old file checksum:</td>
                    <td>&nbsp;</td>
                    <td><input id="oldFileChecksum" type="text"/></td>
                    <td>&nbsp;&nbsp;</td>
                    <td>New file checksum:</td>
                    <td>&nbsp;</td>
                    <td><input id="newFileChecksum" type="text"/></td>
                </tr>
                <tr>
                    <td>Old file checksum type:</td>
                    <td>&nbsp;</td>
                    <td>                        
                        <input type="radio" name="oldFileChecksumType" id="oldFilemd5ChecksumType" value="MD5" checked="yes"/> 
                        <label for="oldFilemd5ChecksumType">MD5</label>
                        <input type="radio" name="oldFileChecksumType" id="oldFilesha1ChecksumType" value="SHA1"/> 
                        <label for="oldFilesha1ChecksumType">SHA1</label>
                    </td>
                    <td>&nbsp;&nbsp;</td>
                    <td>New file checksum type:</td>
                    <td>&nbsp;</td>
                    <td>                        
                        <input type="radio" name="newFileChecksumType" id="newFilemd5ChecksumType" value="MD5" checked="yes"/> 
                        <label for="newFilemd5ChecksumType">MD5</label>
                        <input type="radio" name="newFileChecksumType" id="newFilesha1ChecksumType" value="SHA1"/> 
                        <label for="newFilesha1ChecksumType">SHA1</label>
                    </td>
                </tr>
                <tr>
                    <td>Old file checksum salt (optional):</td>
                    <td>&nbsp;</td>
                    <td><input type="text" id="oldFileChecksumSalt"/></td>
                    <td>&nbsp;&nbsp;</td>
                    <td>New file checksum salt (optional):</td>
                    <td>&nbsp;</td>
                    <td><input type="text" id="newFileChecksumSalt"/></td>
                </tr>
                <tr>
                    <td>Old file checksum request type: (optional):</td>
                    <td>&nbsp;</td>
                    <td>
                        <input type="radio" name="oldFileRequestChecksumType" id="oldFileRequestDisableChecksum" value="disabled" checked="yes"/> 
                        <label for="oldFileRequestDisableChecksum">disable</label>
                        <input type="radio" name="oldFileRequestChecksumType" id="oldFileRequestmd5ChecksumType" value="MD5"/> 
                        <label for="oldFileRequestmd5ChecksumType">MD5</label>
                        <input type="radio" name="oldFileRequestChecksumType" id="oldFileRequestsha1ChecksumType" value="SHA1"/> 
                        <label for="oldFileRequestsha1ChecksumType">SHA1</label>
                    </td>
                    <td>&nbsp;&nbsp;</td>
                    <td>New file checksum request type: (optional):</td>
                    <td>&nbsp;</td>
                    <td>
                        <input type="radio" name="newFileRequestChecksumType" id="newFileRequestDisableChecksum" value="disabled" checked="yes"/> 
                        <label for="newFileRequestDisableChecksum">disable</label>
                        <input type="radio" name="newFileRequestChecksumType" id="newFileRequestmd5ChecksumType" value="MD5"/> 
                        <label for="newFileRequestmd5ChecksumType">MD5</label>
                        <input type="radio" name="newFileRequestChecksumType" id="newFileRequestsha1ChecksumType" value="SHA1"/> 
                        <label for="newFileRequestsha1ChecksumType">SHA1</label>
                    </td>
                </tr>
                <tr>
                    <td>Old file checksum request salt (optional):</td>
                    <td>&nbsp;</td>
                    <td><input type="text" id="oldFileRequestChecksumSalt"/></td>
                    <td>&nbsp;&nbsp;</td>
                    <td>New file checksum requestsalt (optional):</td>
                    <td>&nbsp;</td>
                    <td><input type="text" id="newFileRequestChecksumSalt"/></td>
                </tr>
            </table>
            <input type="submit" value="Replace file"/>
        </form> 

    <div id="status"> </div>
        
    <script>
        $(function(){
            $.getJSON('repo/reposervice/getPillarList/',{}, function(j){
                var options = '';
                for (var i = 0; i < j.length; i++) {
                    options += '<option value="' + j[i].optionValue + '">' + j[i].optionDisplay + '</option>';
                }
                $("select#pillarSelector").html(options);
            })
        })
    </script>    
        
    <script>
        $("#replaceForm").submit(function() {
            var fileID = $("#fileID").val();
            fileID = fileID.replace(/\s+/g, '');
            var pillarID = $("#pillarSelector option:selected").val();
            var fileAddress = $("#fileaddr").val();
            var fileSize = $("#filesize").val();
            var oldFileChecksumVal = $("#oldFileChecksum").val();
            var oldFileChecksumType = $("input[name=oldFileChecksumType]:checked").val();
            var oldFileChecksumSalt = $("#oldFileChecksumSalt").val();
            var oldFileRequestChecksumType = $("input[name=oldFileRequestChecksumType]:checked").val();
            var oldFileRequestChecksumSalt = $("#oldFileRequestChecksumSalt").val(); 
            var newFileChecksumVal = $("#newFileChecksum").val();
            var newFileChecksumType = $("input[name=newFileChecksumType]:checked").val();
            var newFileChecksumSalt = $("#newFileChecksumSalt").val();
            var newFileRequestChecksumType = $("input[name=newFileRequestChecksumType]:checked").val();
            var newFileRequestChecksumSalt = $("#newFileRequestChecksumSalt").val();   	  	
            	
            var command = "repo/reposervice/replaceFile/?fileID=" + fileID + "&pillarID=" + pillarID +
                    "&oldFileChecksum=" + oldFileChecksumVal + "&oldFileChecksumType="+ oldFileChecksumType + 
                    "&oldFileChecksumSalt=" + oldFileChecksumSalt + "&oldFileRequestChecksumType=" + oldFileRequestChecksumSalt + 
                    "&oldFileRequestChecksumSalt=" + oldFileRequestChecksumSalt + "&url=" + fileAddress + "&fileSize=" + fileSize +
                    "&newFileChecksum=" + newFileChecksumVal + "&newFileChecksumType="+ newFileChecksumType + 
                    "&newFileChecksumSalt=" + newFileChecksumSalt + "&newFileRequestChecksumType=" + newFileRequestChecksumSalt + 
                    "&newFileRequestChecksumSalt=" + newFileRequestChecksumSalt;
            	
            $('#status').load(command, function(response, status, xhr) {
                if (status == "error") {
                    $("#status").html(response);
                }
            }).show();	
            
            return true;
        });
    </script> 
        
	<script>
  		function submit() { return ; }
  	</script>     
    </div>
</html>