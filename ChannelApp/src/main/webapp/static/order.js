function getOrderApiUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content");
    return baseUrl + "/api/order";
}

function getClientNamesUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/order/clients";
}

function getChannelNamesUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/order/channels";
}

function loadClientNames(data) {
    var $clientDropdown = $('#clientId');
    for (var client in data) {
        $clientDropdown.append('<option value=' + data[client].id + '>' + data[client].name + '</option>');
    }
}

function loadCustomerNames(data) {
    var $customerDropdown = $('#customerId');
    for (var customer in data) {
        $customerDropdown.append('<option value=' + data[customer].id + '>' + data[customer].name + '</option>');
    }
}

function loadChannelNames(data) {
    var $channelDropdown = $('#channelId');
    var $channelSelectDropdown = $('#channelIdSelect');
    for (var channel in data) {
        if (data[channel].name != "INTERNAL") {
            $channelDropdown.append('<option value=' + data[channel].id + '>' + data[channel].name + '</option>');
            $channelSelectDropdown.append('<option value=' + data[channel].id + '>' + data[channel].name + '</option>');
        }
    }
}

function toggleOrderCreateToModify() {
    document.getElementById("order-entry").style.display = "none";
    document.getElementById("order-validate-button").style.display = "none";

    document.getElementById("order-item-entry").style.display = "block";
    document.getElementById("order-submit-button").style.display = "block";
}

function toggleOrderModifyToCreate() {
    document.getElementById("order-entry").style.display = "block";
    document.getElementById("order-validate-button").style.display = "block";

    document.getElementById("order-item-entry").style.display = "none";
    document.getElementById("order-submit-button").style.display = "none";
    sessionStorage.setItem('orderItemList', JSON.stringify([]));
    sessionStorage.setItem('orderItems', JSON.stringify([]));
}

function saveClient(sel) {
  sessionStorage.setItem("orderingClient", sel.options[sel.selectedIndex].text);
}

function validateOrder(event) {
    var parsedJson = JSON.parse(toJson($("#order-form")));
    parsedJson['channelId'] = sessionStorage.getItem('channelId');
    parsedJson['customerId'] = '1';

    json = JSON.stringify(parsedJson);
    var url = getOrderApiUrl() + "/validate";
    console.log("Validation of Order:",json);
    $.ajax({
        url: url,
        type: 'POST',
        data: json,
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(response) {
            getOrderList();
            document.getElementById('order-details-client').innerHTML = sessionStorage.getItem("orderingClient");
            document.getElementById('order-details-channel-order-id').innerHTML = JSON.parse(json)['channelOrderId'];

            sessionStorage.setItem("orderIdentifier", json);
            toggleOrderCreateToModify();

            getSuccessSnackbar("Order Validated.");
        },
        error: handleAjaxError
    });
    return false;
}

function addOrder(event) {
    var json = toJson($("#order-form"));
    var url = getOrderApiUrl();

    var orderItemString = JSON.parse(sessionStorage.getItem('orderItems'));
    var orderString = JSON.parse(sessionStorage.getItem('orderIdentifier'));
    orderString['channelId'] = sessionStorage.getItem('channelId');

    orderString['orderItemList'] = orderItemString;

    var orderTrimmedString = JSON.stringify(orderString);

    console.log("Final Request: ", orderTrimmedString);
    console.log("Final URL    : ", url);

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
            toggleOrderModifyToCreate();
            getSuccessSnackbar("Order Placed");
        },
        error: handleAjaxError
    });
    return false;
}

sessionStorage.setItem('orderItemList', JSON.stringify([]));
sessionStorage.setItem('orderItems', JSON.stringify([]));

function validateOrderItem(event) {
    var json = toJson($("#order-item-form"));
    var parsedJson = JSON.parse(json);

    parsedJson['channelSkuId'] = parsedJson['channelSkuId'].trim();

    var orderJson = JSON.parse(sessionStorage.getItem("orderIdentifier"));
    $.extend(orderJson, parsedJson);
    json = JSON.stringify(orderJson);

    var url = "/channel/api/order/orderitem/validate";
    var parsedOrderItemList = JSON.parse(sessionStorage.getItem('orderItemList'));
    var orderItems = JSON.parse(sessionStorage.getItem('orderItems'));

    if (parsedOrderItemList.includes(parsedJson['channelSkuId'])) {
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
            parsedOrderItemList.push(parsedJson['channelSkuId']);
            orderItems.push(parsedJson);

            sessionStorage.setItem('orderItems', JSON.stringify(orderItems));
            sessionStorage.setItem('orderItemList', JSON.stringify(parsedOrderItemList));

            getSuccessSnackbar("Item Added");
        },
        error: function(response) {
            parsedOrderItemList.pop();
            orderItems.pop();
            alert(response.responseText);
        }
    });
    return false;
}

function getOrderList() {
    var channelId = sessionStorage.getItem('channelId');
    console.log("SET CHANNEL:",channelId);
    var url = getOrderApiUrl() + "/channel/" + channelId;
    $.ajax({
        url: url,
        type: 'GET',
        success: function(data) {
            displayOrderList(data);
        },
        error: handleAjaxError
    });
}

function displayOrderList(data) {
    var $tbody = $('#order-table').find('tbody');
    $tbody.empty();

    if (data.length == 0) {
        var row = '<tr>' +
            '<td style="text-align:center; font-weight: bold; background-color:#ffebe8;" colspan="7">No Orders        </td>' +
            '</tr>';
        $tbody.append(row);
    }

    for (var i in data) {
        var e = data[i];
        var infoButtonHtml = '<button style="margin-right:2px;" id="infobutton' + e.id + '" class="btn btn-primary btn-sm" onclick="displayOrderDetails(' + e.id + ')"><i class="fa fa-info-circle"></i>&nbspDetails</button>';
        var allocateButtonHtml = '<button style="margin-right:2px;" id="allocbutton' + e.id + '" class="btn btn-primary btn-sm" onclick="allocateOrder(' + e.id + ')"><i class="fa fa-link"></i>&nbspAllocate</button>';
        var invoiceButtonHtml = '<button style="margin-right:2px;" id="invoicebutton' + e.id + '" class="btn btn-primary btn-sm" onclick="invoiceOrder(' + e.id + ')"><i class="fa fa-print"></i>&nbspInvoice</button>';

            var dateCreated = new Date(Date.parse(e.createdAt));
        	var row = '<tr>'
        	+ '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>'
        	+ '<td>' + dateCreated.getDate()+'/'+dateCreated.getMonth()+1+'/'+dateCreated.getFullYear()+' '+dateCreated.getHours()+':'+dateCreated.getMinutes()+':'+dateCreated.getSeconds() + '</td>'+
        	'<td style="text-align:center;">' + e.clientName + '</td>' +
            '<td style="text-align:center;">' + e.customerName + '</td>' +
            '<td>' + e.channelOrderId + '</td>' +
            '<td>' + e.status + '</td>' +
            '<td align="right">' + infoButtonHtml + '</td>' +
            '</tr>';
        $tbody.append(row);
    }
}

function displayOrderDetails(orderId) {
    console.log("ORDER DETAILS: ", orderId)
    var url = getOrderApiUrl() + "/orderId/" + orderId;

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

function displayOrderDetailsTable(data) {
    var $tbody = $('#order-detail-table').find('tbody');
    $tbody.empty();

    if (data.length == 0) {
        var row = '<tr>' +
            '<td style="text-align:center; font-weight: bold; background-color:#ffebe8;" colspan="5">No Order Items</td>' +
            '</tr>';
        $tbody.append(row);
    }

    for (var i in data) {
        var e = data[i];
        var row = '<tr>' +
            '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>' +
            '<td>' + e.channelSkuId + '</td>' +
            '<td>' + e.orderedQuantity + '</td>' +
            '<td>' + e.allocatedQuantity + '</td>' +
            '<td>' + e.fulfilledQuantity + '</td>' +
            '</tr>';
        $tbody.append(row);
    }
    $('#orderDetailsModal').modal('show');
}

function populateDropdown() {
    populateClientDropdown();
    populateChannelDropdown();
    return false;
}

function populateChannelDropdown() {
    var channelUrl = getChannelNamesUrl();
    $.ajax({
        url: channelUrl,
        type: 'GET',
        success: function(data) {
            loadChannelNames(data);
        },
        error: handleAjaxError
    });
    return false;
}

function populateClientDropdown() {
    var clientUrl = getClientNamesUrl();

    $.ajax({
        url: clientUrl,
        type: 'GET',
        success: function(data) {
            loadClientNames(data);
        },
        error: handleAjaxError
    });
    return false;
}

function resetOrderModal() {
    sessionStorage.setItem('orderItemList', JSON.stringify([]));
    sessionStorage.setItem('orderItems', JSON.stringify([]));
    toggleOrderModifyToCreate();
}

function setChannel() {
    var parsedJson = JSON.parse(toJson($("#channelSelectForm")));
    sessionStorage.setItem('channelId', parsedJson['channelId']);
    document.getElementById("channelIdSelect").disabled = true;
    document.getElementById("channel-select-btn").disabled = true;

    document.getElementById("create-order-div").style.visibility = "visible";
    document.getElementById("order-table").style.visibility = "visible";
    getOrderList();
    getSuccessSnackbar("Success");
    return false;
}

//Initialization Code
function init() {
    document.getElementById("channel-select-btn").disabled = false;
    $('#channelSelectForm').submit(setChannel);

    $('#validate-order').click(validateOrder);
    $('#add-order-item').click(validateOrderItem);

    $('#add-order').click(addOrder);

    $('#refresh-data').click(getOrderList);
}

$(document).ready(init);
$(document).ready(populateDropdown);