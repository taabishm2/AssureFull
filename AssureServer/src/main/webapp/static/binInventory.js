//Returns the URL to call the APIs
function getBinSkuApiUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/binSku";
}

//Add a binSku
function addBinSku(event){
	var $form = $("#binSku-form");
	var json = toJson($form);
	var url = getBinSkuApiUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getBinSkuList();
	   		$('#exampleModal').modal('toggle');
	   		getSuccessSnackbar("Bin Inventory Item Created.");
	   },
	   error: handleAjaxError
	});
	return false;
}

//GET Method: Retrieve all BinSkus
function getBinSkuList(){
	var url = getBinSkuApiUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBinSkuList(data);
	   },
	   error: handleAjaxError
	});
}

//Display table of BinSkus
function displayBinSkuList(data){
	var $tbody = $('#binSku-table').find('tbody');
	$tbody.empty();

    if(data.length == 0){
	    var row = '<tr>'
        + '<td style="text-align:center; font-weight: bold; background-color:#ffebe8;" colspan="4">No Bin Inventory Items</td>'
        + '</tr>';
        $tbody.append(row);
    }

	for(var i in data){
		var e = data[i];
		var row = '<tr>'
		+ '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>'
		+ '<td>' + e.binId + '</td>'
		+ '<td>' + e.globalSkuId + '</td>'
		+ '<td style="text-align:center;">' + e.quantity + '</td>'
		+ '</tr>';
		console.log(row);
        $tbody.append(row);
	}
}

//Initialization Code
function init(){
	$('#binSku-form').submit(addBinSku);
	$('#refresh-data').click(getBinSkuList);
}

console.log(getBinSkuApiUrl());
$(document).ready(init);

