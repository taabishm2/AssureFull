function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/product";
}

function getClientNamesUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/consumer/clients";
}

function updateProduct(event){
	$('#edit-product-modal').modal('toggle');
	var url = getProductUrl() + "/" + currentlySelectedClientId + "/" + currentlySelectedClientSkuId;

	var $form = $("#product-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getProductList();
	   		getSuccessSnackbar("Product Updated.");
	   },
	   error: handleAjaxError
	});
	return false;
}

function getProductList(){
	var url = getProductUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayProductList(data);  
	   },
	   error: handleAjaxError
	});
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;
var clientId;
var addListRequestBody = [];

var currentlySelectedClientId;
var currentlySelectedClientSkuId;

function processData(){
    var $form = $("#product-form");
	var clientJson = toJson($form);

	var obj = JSON.parse(clientJson);
	clientId = obj["clientId"];

	var file = $('#productFile')[0].files[0];
	console.log("FILE: ",file);
	readFileData(file, readFileDataCallback);
	return false;
}

function readFileDataCallback(results){
	fileData = results.data;
	console.log("FILE DATA: ",fileData);
	uploadRows();
}

function uploadRows(){
	updateUploadDialog();
	var url = getProductUrl()+"/list/"+clientId;

    var formList = [];
    var processCount;
	for(processCount=0; processCount<fileData.length; processCount++){
	    formList.push(fileData[processCount]);
	}

	var json = JSON.stringify(formList);
	//Make ajax call
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	        getProductList();
	        $('#exampleModal').modal('toggle');
	   		getSuccessSnackbar("Products Added.");
	   },
	   error: function(response){
	   		//row.error=response.responseText
	   		//errorData.push(row);
	   		//uploadRows();
	   		alert(response.responseText);
	   }
	});
	return false;
}

function downloadErrors(){
	writeFileData(errorData);
}

//UI DISPLAY METHODS
function displayProductList(data){
	var $tbody = $('#product-table').find('tbody');
	$tbody.empty();

	if(data.length == 0){
    	    var row = '<tr>'
            + '<td style="text-align:center; font-weight: bold; background-color:#ffebe8;" colspan="8">No Products Created</td>'
            + '</tr>';
            $tbody.append(row);
    	}

	for(var i in data){
		var e = data[i];
		var buttonHtml =  '<button class="btn btn-primary btn-sm" onclick="displayEditProduct(' + e.clientId + ',\'' + e.clientSkuId + '\')"><i class="fa fa-wrench" aria-hidden="true"></i></button>'
		var row = '<tr>'
		+ '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>'
		+ '<td style="text-align:center; font-weight: bold;">' + e.clientId + '</td>'
		+ '<td>' + e.clientSkuId + '</td>'
		+ '<td>'  + e.name + '</td>'
		+ '<td>'  + e.brandId + '</td>'
		+ '<td>'  + e.mrp + '</td>'
		+ '<td>'  + e.description + '</td>'
		+ '<td align="right">' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

function displayEditProduct(clientId, clientSkuId){
	var url = getProductUrl() + "/" + clientId + "/" + clientSkuId;
	currentlySelectedClientId = clientId;
	currentlySelectedClientSkuId = clientSkuId;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayProduct(data);   
	   },
	   error: handleAjaxError
	});	
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#productFile');
	$file.val('');
	$('#productFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Update counts	
	updateUploadDialog();
}

function updateUploadDialog(){
	$('#rowCount').html("" + fileData.length);
	$('#processCount').html("" + processCount);
	$('#errorCount').html("" + errorData.length);
}

function updateFileName(){
	var $file = $('#productFile');
	var fileName = $file.val();
	$('#productFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog(); 	
	$('#upload-product-modal').modal('toggle');
}

function displayProduct(data){
	$("#product-edit-form input[name=name]").val(data.name);	
	$("#product-edit-form input[name=brandId]").val(data.brandId);
	$("#product-edit-form input[name=mrp]").val(data.mrp);
	$("#product-edit-form input[name=description]").val(data.description);
	$('#edit-product-modal').modal('toggle');
}


function loadClientNames(data){
	var $clientDropdown = $('#clientId');
	for(var clientName in data){
		var clientId = data[clientName];
		var option = '<option value='+data[clientName].id+'>'
		+ data[clientName].name
		+ '</option>';
        $clientDropdown.append(option);
	}
}

function populateDropdown(){
    var clientUrl = getClientNamesUrl();
    $.ajax({
           url: clientUrl,
           type: 'GET',
           success: function(data) {
                loadClientNames(data);
           },
           error: handleAjaxError
        });
}

//INITIALIZATION CODE
function init(){
	$('#product-edit-form').submit(updateProduct);
	$('#refresh-data').click(getProductList);
	$('#upload-data').click(displayUploadData);
	$('#product-form').submit(processData);
	$('#download-errors').click(downloadErrors);
    $('#productFile').on('change', updateFileName)

}

$(document).ready(init);
$(document).ready(getProductList);
$(document).ready(populateDropdown);
