function getOrderApiUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	return baseUrl + "/api/order";
}
function getOrderItemApiUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	return baseUrl + "/api/orderItem";
}
function getClientNamesUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/consumer/clients";
}
function getCustomerNamesUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/consumer/customers";
}
function getChannelNamesUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/channel";
}

function toggleOrderCreateToModify(){
    $('#orderModal').modal('hide');
    $('#orderItemModal').modal('show');
}

function resetModals(){
    $('#orderModal').modal('hide');
    $('#orderItemModal').modal('hide');
    $("#order-form")[0].reset();
    $("#order-item-form")[0].reset();
}

function validateOrder(event){
	var parsedJson = JSON.parse(toJson($("#order-form")));
	var url = getOrderApiUrl()+"/validate";
    json = JSON.stringify(parsedJson);

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		sessionStorage.setItem("orderIdentifier",JSON.stringify(parsedJson));
	   		toggleOrderCreateToModify();

	   		getSuccessSnackbar("Order Validated");
	   },
	   error: handleAjaxError
	});
	return false;
}

function processData(){
	var file = $('#orderItemFile')[0].files[0];
	readFileData(file, readFileDataCallback);
	return false;
}

function readFileDataCallback(results){
	fileData = results.data;
	uploadRows();
}

function uploadRows(){
    var formList = [];
    var processCount;
	for(processCount=0; processCount<fileData.length; processCount++){
	    formList.push(fileData[processCount]);
	}

	validateCsv(formList);
}

function validateCsv(formList){
        console.log("Validation");
        var clientId = JSON.parse(sessionStorage.getItem('orderIdentifier'))['clientId'];
        var channelId = JSON.parse(sessionStorage.getItem('orderIdentifier'))['channelId'];
        console.log(clientId);
        var url = getOrderApiUrl() + "/orderItems/validate/" + clientId + "/" + channelId;

    	var json = JSON.stringify(formList);
        console.log("JSON:",json);
    	//Make ajax call
    	$.ajax({
    	   url: url,
    	   type: 'POST',
    	   data: json,
    	   headers: {
           	'Content-Type': 'application/json'
           },
    	   success: function(response) {
    	        uploadOrder(formList);
    	        getSuccessSnackbar("Order Placed");
    	   },
    	   error: function(response){
    	        errorButtonActivate("http://127.0.0.1:9090/"+JSON.parse(response.responseText)['message']);
    	   		alert("Errors in CSV");
    	   		var index = array.indexOf(item);
                if (index !== -1) array.splice(index, 1);
    	   }
    	});
    	return false;
}

function errorButtonActivate(link){
    document.getElementById("download-errors").disabled = false;
    document.getElementById('error-file-link').href = link;
}

function uploadOrder(formList){
    var orderDetails = JSON.parse(sessionStorage.getItem('orderIdentifier'));
    orderDetails['orderItemList'] = formList;
    json = JSON.stringify(orderDetails);

    console.log("REQUESTED: ",json);
    var url = getOrderApiUrl();
    	$.ajax({
    	   url: url,
    	   type: 'POST',
    	   data: json,
    	   headers: {
           	'Content-Type': 'application/json'
           },
    	   success: function(response) {
    	   		resetModals();
    	   },
    	   error: handleAjaxError
    	});
    	return false;
}

function getSearchOrderList(){
    var $form = $("#search-param-form");

//    var fromDate = new Date(Date.parse(document.getElementById("fromDateSearch").value + 'T00:00'));
//    var toDate = new Date(Date.parse(document.getElementById("toDateSearch").value + 'T24:00'))
    var json = JSON.parse(toJson($form));
//    json["fromDate"] = fromDate.toISOString();
//    json["toDate"] = toDate.toISOString();

    json = JSON.stringify(json);
    console.log(json);

	var url = getOrderApiUrl() + '/search';

    	$.ajax({
    	   url: url,
    	   type: 'POST',
    	   data: json,
    	   headers: {
           	'Content-Type': 'application/json'
           },
    	   success: function(response) {
    	   		displayOrderList(response);
    	   },
    	   error: handleAjaxError
    	});
    	return false;
}

//Display table of Orders
function displayOrderList(data){
	var $tbody = $('#order-table').find('tbody');
	$tbody.empty();

    if(data.length == 0){
	    var row = '<tr>'
        + '<td style="text-align:center; font-weight: bold; background-color:#ffebe8;" colspan="8">No Orders</td>'
        + '</tr>';
        $tbody.append(row);
    }

	for(var i in data){
		var e = data[i];
		var infoButtonHtml = '<button style="margin-right:2px;" id="infobutton'+e.id+'" class="btn btn-primary btn-sm" onclick="displayOrderDetails(' + e.id + ')"><i class="fa fa-info-circle"></i>&nbspDetails</button>';
		var allocateButtonHtml = '<button style="margin-right:2px;" id="allocbutton'+e.id+'" class="btn btn-primary btn-sm" onclick="allocateOrder(' + e.id + ')"><i class="fa fa-link"></i>&nbspAllocate</button>';
		var invoiceButtonHtml = '<button style="margin-right:2px;" id="invoicebutton'+e.id+'" class="btn btn-primary btn-sm" onclick="invoiceOrder(' + e.id + ',\'' + e.channelName + '\')"><i class="fa fa-print"></i>&nbspInvoice</button>';

		var row = '<tr>'
		+ '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>'
		+ '<td>' + e.dateCreated + '</td>'
		+ '<td>' + e.clientName + '</td>'
		+ '<td>' + e.customerName + '</td>'
		+ '<td>' + e.channelName + '</td>'
		+ '<td>' + e.channelOrderId + '</td>'
		+ '<td>' + e.status + '</td>'
	    + '<td align="right">' + infoButtonHtml + allocateButtonHtml + invoiceButtonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);

                if(e.status == "CREATED"){
                document.getElementById("invoicebutton"+e.id).disabled = true;
                document.getElementById("allocbutton"+e.id).disabled = false;
                        }

                if(e.status == "ALLOCATED"){
                document.getElementById("invoicebutton"+e.id).disabled = false;
                document.getElementById("allocbutton"+e.id).disabled = true;

                }
                if(e.status == "FULFILLED"){
                document.getElementById("invoicebutton"+e.id).disabled = false;
                document.getElementById("allocbutton"+e.id).disabled = true;

                        }
	}

		document.getElementById("order-table").style.visibility = "visible";
    	document.getElementById("refresh-data").disabled = false;
    	getSuccessSnackbar("Refreshed");
    	return false;
}

function displayOrderDetails(orderId){
    var url = getOrderItemApiUrl() + "/orderId/" + orderId;

    	$.ajax({
    	   url: url,
    	   type: 'GET',
    	   success: function(data) {
    	   		displayOrderDetailsTable(data);
    	   },
    	   error: handleAjaxError
    	});
    	return false;
}
function displayOrderDetailsTable(data){
        	var $tbody = $('#order-detail-table').find('tbody');
        	$tbody.empty();

if(data.length == 0){
    	    var row = '<tr>'
            + '<td style="text-align:center; font-weight: bold; background-color:#ffebe8;" colspan="5">No Order Items</td>'
            + '</tr>';
            $tbody.append(row);
    	}

	for(var i in data){
		var e = data[i];
		var row = '<tr>'
		+ '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>'
		+ '<td>' + e.clientSkuId + '</td>'
		+ '<td>' + e.orderedQuantity + '</td>'
		+ '<td>' + e.allocatedQuantity + '</td>'
		+ '<td>' + e.fulfilledQuantity + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
	$('#orderDetailsModal').modal('show');
}

function allocateOrder(orderId){
    var url = getOrderApiUrl() + "/allocate/" + orderId;
    	$.ajax({
    	   url: url,
    	   type: 'POST',
    	   data: '',
    	   headers: {
           	'Content-Type': 'application/json'
           },
    	   success: function(response) {
    	        getSearchOrderList();
        	   getSuccessSnackbar("Order Allocated");
    	   },
    	   error: handleAjaxError
    	});
    	return false;
}

function invoiceOrder(orderId, channelName){
    var url = getOrderApiUrl() + "/invoice/" + orderId;
        	$.ajax({
        	   url: url,
        	   type: 'POST',
        	   data: '',
        	   headers: {
               	'Content-Type': 'application/json'
               },
        	   success: function(response) {
            	   getSuccessSnackbar("Invoice Generated");
            	   displayDownloadModal(orderId, channelName);
            	   getSearchOrderList();
            	   //alert("C://Users//Tabish//Documents//Repos//Increff//AssureServer//src//main//resources//output//"+orderId+".pdf");
        	   },
        	   error: handleAjaxError
        	});
        	return false;
}

function displayDownloadModal(orderId, channelName){
    var destination = 'ChannelApp';
    if(channelName == 'INTERNAL'){
        destination = 'AssureServer';
    }

	var $tbody = $('#invoice-link-table').find('tbody');
	$tbody.empty();
	var row = '<tr>'
    		+ '<td style="text-align:center; font-weight: bold;">' + orderId + '</td>'
    	    + '<td align="center">'
    	    + '<a href="http://127.0.0.1:9090/'+orderId+'.pdf">'
    	    + '<i class="fa fa-download" aria-hidden="true"></i>Download'
    	    + '</a>'
    	    + '</td>'
    		+ '</tr>';
	$tbody.append(row);
	$('#downloadLinkModal').modal('show');
}

function loadClientNames(data){
	var $clientDropdown = $('#clientId');
	var $clientSearchDropdown = $('#clientIdSearch');

	for(var client in data){
        $clientDropdown.append('<option value=' + data[client].id + '>' + data[client].name + '</option>');
        $clientSearchDropdown.append('<option value=' + data[client].id + '>' + data[client].name + '</option>');
	}
}

function loadCustomerNames(data){
	var $customerDropdown = $('#customerId');
	var $customerSearchDropdown = $('#customerIdSearch');

	for(var customer in data){
        $customerDropdown.append('<option value=' + data[customer].id + '>' + data[customer].name + '</option>');
        $customerSearchDropdown.append('<option value=' + data[customer].id + '>' + data[customer].name + '</option>');
	}
}

function loadChannelNames(data){
	var $channelDropdown = $('#channelId');
	var $channelSearchDropdown = $('#channelIdSearch');

	for(var channel in data){
        $channelDropdown.append('<option value=' + data[channel].id + '>' + data[channel].name + '</option>');
        $channelSearchDropdown.append('<option value=' + data[channel].id + '>' + data[channel].name + '</option>');
	}
}

function populateDropdown(){
    var clientUrl = getClientNamesUrl();
    var customerUrl = getCustomerNamesUrl();
    var channelUrl = getChannelNamesUrl();
    
    $.ajax({
           url: clientUrl,
           type: 'GET',
           success: function(data) {
                loadClientNames(data);
           },
           error: handleAjaxError
    });
        
        $.ajax({
               url: customerUrl,
               type: 'GET',
               success: function(data) {
                    loadCustomerNames(data);
               },
               error: handleAjaxError
            });
            
     $.ajax({
            url: channelUrl,
            type: 'GET',
            success: function(data) {
                 loadChannelNames(data);
            },
            error: handleAjaxError
         });
}

function resetOrderModal(){
    sessionStorage.clear();
    toggleOrderModifyToCreate();
}

//Initialization Code
function init(){
    document.getElementById("download-errors").disabled = true;
    document.getElementById("refresh-data").disabled = true;

	$('#order-form').submit(validateOrder);
	$('#order-item-form').submit(processData);

    $('#search-param-form').submit(getSearchOrderList);

	$('#refresh-data').click(getSearchOrderList);
}

$(document).ready(init);
$(document).ready(populateDropdown);

