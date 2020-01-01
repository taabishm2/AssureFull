//Returns the URL to call the APIs
function getBinApiUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/bin";
}

function addBin(event){
    var $form = $("#bin-form");
	var json = toJson($form);
    var bins = $("#binQuantity").val();
	var url = getBinApiUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: bins,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getBinList();
	   },
	   error: handleAjaxError
	});
	return false;
}

//GET Method: Retrieve all Bins
function getBinList(){
	var url = getBinApiUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBinList(data);
	   },
	   error: handleAjaxError
	});
}

//Display table of Bins
function displayBinList(data){
	var $tbody = $('#bin-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var row = '<tr>'
		+ '<td style="text-align:center; font-weight: bold;">' + e + '</td>'
		+ '<td style="text-align:center;">CREATED</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

//Initialization Code
function init(){
	$('#add-bin').click(addBin);
	$('#refresh-data').click(getBinList);
}

$(document).ready(init);
$(document).ready(getBinList);