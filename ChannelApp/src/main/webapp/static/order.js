//Returns the URL to call the APIs
function getOrderApiUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	return baseUrl + "/api/order";
}

function getClientNamesUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/order/clients";
}

function getChannelNamesUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/order/channels";
}

//Add a order
function addOrder(event){
	var $form = $("#order-form");
	var json = toJson($form);

	var url = getOrderApiUrl();

    var orderItemString = sessionStorage.getItem('orderItems');
    var requestString = orderItemString.substring(0,orderItemString.length-1) + "]";

    var orderString = JSON.parse(sessionStorage.getItem('orderIdentifier'));
    orderString['customerId'] = '3';
    orderString = JSON.stringify(orderString);
    var orderTrimmedString = orderString.substring(0,orderString.length-1) + ", \"orderItemList\":" + requestString + "}";

    console.log("Request: ",orderTrimmedString);
    console.log("URl: ",url);

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: orderTrimmedString,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getOrderList();
	   		$('#exampleModal').modal('toggle');
	   		getSuccessSnackbar("Order Placed.");
	   },
	   error: handleAjaxError
	});
	return false;
}

function validateOrder(event){
	var $form = $("#order-form");
	var json = toJson($form);
	var parsedJson = JSON.parse(json);

	var url = getOrderApiUrl()+"/validate";

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getOrderList();

	   		sessionStorage.setItem("orderIdentifier",JSON.stringify(parsedJson));
	   		toggleOrderCreateToModify();

	   		getSuccessSnackbar("Order Validated.");
	   },
	   error: handleAjaxError
	});
	return false;
}

sessionStorage.setItem('orderItems','[');
sessionStorage.setItem('orderItemList', JSON.stringify([]));

function toggleOrderCreateToModify(){
document.getElementById('clientId').setAttribute('readonly','true');
	   		            document.getElementById("order-entry").style.display = "none";
                        document.getElementById("order-validate-button").style.display = "none";

                        document.getElementById("order-item-entry").style.display = "block";
                        document.getElementById("order-submit-button").style.display = "block";
}

function toggleOrderModifyToCreate(){
document.getElementById('clientId').setAttribute('readonly','true');
	   		            document.getElementById("order-entry").style.display = "block";
                        document.getElementById("order-validate-button").style.display = "block";

                        document.getElementById("order-item-entry").style.display = "none";
                        document.getElementById("order-submit-button").style.display = "none";
}

function validateOrderItem(event){
	var $form = $("#order-item-form");
	var json = toJson($form);
	var parsedJson = JSON.parse(json);

    parsedJson['globalSkuId'] = parsedJson['globalSkuId'].trim();

    console.log(sessionStorage.getItem('orderItems'));

	var orderDetails = sessionStorage.getItem("orderIdentifier");
	var orderJson = JSON.parse(orderDetails);

	$.extend(orderJson, parsedJson);
	json = JSON.stringify(orderJson);

	var url = "/channel/api/order/orderitem/validate";
	console.log(parsedJson);

	var parsedOrderItemList = JSON.parse(sessionStorage.getItem('orderItemList'));
	console.log(parsedOrderItemList, sessionStorage.getItem('orderItemList'));
	if(parsedOrderItemList.includes(parsedJson['globalSkuId'])){
	    alert("Product already added");
	    return false;
	}

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	        parsedOrderItemList.push(parsedJson['globalSkuId']);
       	    sessionStorage.setItem('orderItemList', JSON.stringify(parsedOrderItemList));

            sessionStorage.setItem('orderItems',sessionStorage.getItem('orderItems')+JSON.stringify(parsedJson)+',');
            console.log(sessionStorage.getItem('orderItems'));
	   		getSuccessSnackbar("Item Added");
	   },
	   error: handleAjaxError
	});
	return false;
}

//GET Method: Retrieve all Orders
function getOrderList(){
	var url = getOrderApiUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayOrderList(data);
	   },
	   error: handleAjaxError
	});
}

//Display table of Orders
function displayOrderList(data){
	var $tbody = $('#order-table').find('tbody');
	$tbody.empty();

    if(data.length == 0){
	    var row = '<tr>'
        + '<td style="text-align:center; font-weight: bold; background-color:#ffebe8;" colspan="7">No Bin Inventory Items</td>'
        + '</tr>';
        $tbody.append(row);
    }

	for(var i in data){
		var e = data[i];
		var infoButtonHtml = '<button style="margin-right:2px;" id="infobutton'+e.id+'" class="btn btn-primary btn-sm" onclick="displayOrderDetails(' + e.id + ')"><i class="fa fa-info-circle"></i>&nbspDetails</button>';
		var allocateButtonHtml = '<button style="margin-right:2px;" id="allocbutton'+e.id+'" class="btn btn-primary btn-sm" onclick="allocateOrder(' + e.id + ')"><i class="fa fa-link"></i>&nbspAllocate</button>';
		var invoiceButtonHtml = '<button style="margin-right:2px;" id="invoicebutton'+e.id+'" class="btn btn-primary btn-sm" onclick="invoiceOrder(' + e.id + ')"><i class="fa fa-print"></i>&nbspInvoice</button>';

		var row = '<tr>'
		+ '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>'
		+ '<td style="text-align:center;">' + e.clientId + '</td>'
		+ '<td style="text-align:center;">' + e.customerId + '</td>'
		+ '<td>' + e.channelId + '</td>'
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
}

function displayOrderDetails(orderId){
    var url = getOrderApiUrl() + "/details/" + orderId;
    //TODO:Implement
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
    	       getOrderList();
        	   getSuccessSnackbar("Order Allocated");
    	   },
    	   error: handleAjaxError
    	});
    	return false;
}

function invoiceOrder(orderId){
    var url = getOrderApiUrl() + "/invoice/" + orderId;
        	$.ajax({
        	   url: url,
        	   type: 'POST',
        	   data: '',
        	   headers: {
               	'Content-Type': 'application/json'
               },
        	   success: function(response) {
        	       getOrderList();
            	   getSuccessSnackbar("Invoice Generated");
            	   console.log("Displaying MODAL");
            	   displayDownloadModal(orderId);
            	},
        	   error: handleAjaxError
        	});
        	return false;
}

function displayDownloadModal(orderId){
	var $tbody = $('#invoice-link-table').find('tbody');
	$tbody.empty();
	var row = '<tr>'
    		+ '<td style="text-align:center; font-weight: bold;">' + orderId + '</td>'
    	    + '<td align="center">'
    	    + '<a href="C://Users//Tabish//Documents//Repos//Increff//AssureServer//src//main//resources//output//'+orderId+'.pdf">'
    	    + '<i class="fa fa-download" aria-hidden="true"></i>Download'
    	    + '</a>'
    	    + '</td>'
    		+ '</tr>';
	$tbody.append(row);
	$('#downloadLinkModal').modal('show');
}

function loadClientNames(data){
	var $clientDropdown = $('#clientId');
	for(var client in data){
        $clientDropdown.append('<option value=' + data[client].id + '>' + data[client].name + '</option>');
	}
}

function loadCustomerNames(data){
	var $customerDropdown = $('#customerId');
	for(var customer in data){
        $customerDropdown.append('<option value=' + data[customer].id + '>' + data[customer].name + '</option>');
	}
}

function loadChannelNames(data){
	var $channelDropdown = $('#channelId');
	for(var channel in data){
        $channelDropdown.append('<option value=' + data[channel].id + '>' + data[channel].name + '</option>');
	}
}

function populateDropdown(){
    populateClientDropdown();
    populateChannelDropdown();
}

function populateChannelDropdown(){
    var channelUrl = getChannelNamesUrl();
         $.ajax({
                url: channelUrl,
                type: 'GET',
                success: function(data) {
                     loadChannelNames(data);
                },
                error: handleAjaxError
             });
}

function populateClientDropdown(){
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

function resetOrderModal(){
    sessionStorage.clear();
    toggleOrderModifyToCreate();
}

//Initialization Code
function init(){
	$('#validate-order').click(validateOrder);
	$('#add-order-item').click(validateOrderItem);

	$('#add-order').click(addOrder);

	$('#refresh-data').click(getOrderList);
}

$(document).ready(init);
$(document).ready(getOrderList);
$(document).ready(populateDropdown);

