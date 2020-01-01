//Returns the URL to call the APIs
function getBinInventoryApiUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/binSku";
}

//Add a binInventory
function addBinInventory(event){
	var $form = $("#binInventory-form");
	var json = toJson($form);
	var url = getBinInventoryApiUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getBinInventoryList();
	   },
	   error: handleAjaxError
	});
	return false;
}

//GET Method: Retrieve all BinInventorys
function getBinInventoryList(){
	var url = getBinInventoryApiUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBinInventoryList(data);
	   },
	   error: handleAjaxError
	});
}

//Display table of BinInventorys
function displayBinInventoryList(data){
	var $tbody = $('#binInventory-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var row = '<tr>'
		+ '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>'
		+ '<td>' + e.binId + '</td>'
		+ '<td>' + e.globalSkuId + '</td>'
		+ '<td>' + e.quantity + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

//Initialization Code
function init(){
	$('#add-binInventory').click(addBinInventory);
	$('#refresh-data').click(getBinInventoryList);
}

$(document).ready(init);
$(document).ready(getBinInventoryList);

