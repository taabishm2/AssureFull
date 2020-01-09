//Returns the URL to call the APIs
function getOrderApiUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content");
	return baseUrl + "/api/order";
}

//Add a order
function addOrder(event){

	var $form = $("#order-form");
	var json = toJson($form);
	var url = getOrderApiUrl();

	sessionStorage.setItem('orderItems',sessionStorage.getItem('orderItems')+']');


    var orderJson = JSON.parse(sessionStorage.getItem('orderIdentifier'))
    orderJson["orderItemList"] = sessionStorage.getItem('orderItems');

    console.log(orderJson);
    console.log(JSON.stringify(orderJson));

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getOrderList();
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
	   		document.getElementById('clientId').setAttribute('readonly','true');
	   		document.getElementById('customerId').setAttribute('readonly','true');
	   		document.getElementById('channelId').setAttribute('readonly','true');
	   		document.getElementById('channelOrderId').setAttribute('readonly','true');

	   		sessionStorage.setItem("orderIdentifier",JSON.stringify(parsedJson));

            document.getElementById("order-entry").style.display = "none";
            document.getElementById("order-validate-button").style.display = "none";

            document.getElementById("order-item-entry").style.display = "block";
            document.getElementById("order-submit-button").style.display = "block";

	   		getSuccessSnackbar("Order Validated.");
	   },
	   error: handleAjaxError
	});
	return false;
}

sessionStorage.setItem('orderItems','[');

function validateOrderItem(event){
	var $form = $("#order-item-form");
	var json = toJson($form);

	sessionStorage.setItem('orderItems',sessionStorage.getItem('orderItems')+json+',')

	var orderDetails = sessionStorage.getItem("orderIdentifier");
	var orderJson = JSON.parse(orderDetails);

	var parsedJson = JSON.parse(json);

	$.extend(parsedJson, orderJson);
	json = JSON.stringify(parsedJson);

	var url = getOrderApiUrl()+"/orderitem/validate";

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getOrderList();

	   		sessionStorage.setItem(parsedJson['globalSkuId'],'json');

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
        + '<td style="text-align:center; font-weight: bold; background-color:#ffebe8;" colspan="4">No Bin Inventory Items</td>'
        + '</tr>';
        $tbody.append(row);
    }

	for(var i in data){
		var e = data[i];
		var buttonHtml =  '<button class="btn btn-primary btn-sm" onclick="displayOrderDetails(' + e.id + ')"><i class="fa fa-info-circle" aria-hidden="true"></i>&nbspDetails</button>'
		var row = '<tr>'
		+ '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>'
		+ '<td style="text-align:center;">' + e.clientId + '</td>'
		+ '<td style="text-align:center;">' + e.customerId + '</td>'
		+ '<td>' + e.channelId + '</td>'
		+ '<td>' + e.channelOrderId + '</td>'
	    + '<td align="right">' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
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

