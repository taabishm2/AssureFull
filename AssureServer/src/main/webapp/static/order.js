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

    var orderItemString = sessionStorage.getItem('orderItems');
    var requestString = orderItemString.substring(0,orderItemString.length-1) + "]";

    var orderString = sessionStorage.getItem('orderIdentifier');
    var orderTrimmedString = orderString.substring(0,orderString.length-1) + ", \"orderItemList\":" + requestString + "}";

    console.log("REEEEEEEQUESSSSSTTTT",orderTrimmedString);

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

	   		sessionStorage.setItem("orderIdentifier",JSON.stringify(parsedJson));
	   		toggleOrderCreateModify();

	   		getSuccessSnackbar("Order Validated.");
	   },
	   error: handleAjaxError
	});
	return false;
}

sessionStorage.setItem('orderItems','[');
sessionStorage.setItem('orderItemList', JSON.stringify([]));

function toggleOrderCreateModify(){
document.getElementById('clientId').setAttribute('readonly','true');
	   		document.getElementById('customerId').setAttribute('readonly','true');
	   		document.getElementById('channelId').setAttribute('readonly','true');
	   		document.getElementById('channelOrderId').setAttribute('readonly','true');

	   		            document.getElementById("order-entry").style.display = "none";
                        document.getElementById("order-validate-button").style.display = "none";

                        document.getElementById("order-item-entry").style.display = "block";
                        document.getElementById("order-submit-button").style.display = "block";
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

	var url = "/assure/api/orderitem/validate";
	console.log(parsedJson);

	var parsedOrderItemList = JSON.parse(sessionStorage.getItem('orderItemList'));
	console.log(parsedOrderItemList, sessionStorage.getItem('orderItemList'));
	if(parsedOrderItemList.includes(parsedJson['globalSkuId'])){
	    alert("Product already added");
	    return false;
	}

	parsedOrderItemList.push(parsedJson['globalSkuId']);
	sessionStorage.setItem('orderItemList', JSON.stringify(parsedOrderItemList));

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
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
		var buttonHtml =  '<button style="margin-right:2px;" class="btn btn-primary btn-sm" onclick="displayOrderDetails(' + e.id + ')"><i class="fa fa-info-circle"></i>&nbspDetails</button><button style="margin-right:2px;" class="btn btn-primary btn-sm" onclick="allocateOrder(' + e.id + ')"><i class="fa fa-link"></i>&nbspAllocate</button><button style="margin-right:2px;" class="btn btn-primary btn-sm" onclick="invoiceOrder(' + e.id + ')"><i class="fa fa-print"></i>&nbspInvoice</button>'
		var row = '<tr>'
		+ '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>'
		+ '<td style="text-align:center;">' + e.clientId + '</td>'
		+ '<td style="text-align:center;">' + e.customerId + '</td>'
		+ '<td>' + e.channelId + '</td>'
		+ '<td>' + e.channelOrderId + '</td>'
		+ '<td>' + e.status + '</td>'
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

