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
    	   $('#exampleModal').modal('toggle');
    	   getSuccessSnackbar("Bins Created");
    	   displayNewBinInfo(response);
	   },
	   error: handleAjaxError
	});
	return false;
}

function displayNewBinInfo(response){
    	var $tbody = $('#new-bin-info-table').find('tbody');
    	$tbody.empty();

    	var arrayLength = response.length;
        for (var i = 0; i < arrayLength; i++) {
        console.log(i,response,response[i]);
                	var row = '<tr>'
                    		+ '<td style="text-align:center; font-weight: bold;">' + response[i] + '</td>'
                    	     + '<td align="center" style="color:#4BB543"><i class="fa fa-check-circle-o"></i>&nbspCreated</td>'
                    		+ '</tr>';
                	$tbody.append(row);
        }

    	$('#binIdModal').modal('show');
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

	if(data.length == 0){
	    var row = '<tr>'
        + '<td style="text-align:center; font-weight: bold; background-color:#ffebe8;" colspan="2">No Bins Created</td>'
        + '</tr>';
        $tbody.append(row);
	}

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
	$('#bin-form').submit(addBin);
	$('#refresh-data').click(getBinList);
}

$(document).ready(init);
$(document).ready(getBinList);