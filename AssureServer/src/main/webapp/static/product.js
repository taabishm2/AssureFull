function getProductUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/product";
}

function getClientNamesUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/consumer/clients";
}

function updateProduct(event) {
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
            getProductListByClient();
            getSuccessSnackbar("Success");
        },
        error: handleAjaxError
    });
    return false;
}

function getProductListByClient() {
    var $form = $("#productDisplayForm");
    var json = toJson($form);

    var url = getProductUrl() + "/client/" + JSON.parse(json)['clientId'];
    console.log(url);
    $.ajax({
        url: url,
        type: 'GET',
        success: function(data) {
            document.getElementById("product-table").style.visibility = "visible";
            document.getElementById("refresh-data").disabled = false;
            displayProductList(data);
        },
        error: handleAjaxError
    });
    return false;
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;
var clientId;
var addListRequestBody = [];

var currentlySelectedClientId;
var currentlySelectedClientSkuId;

function processData() {
    var $form = $("#product-form");
    var clientJson = toJson($form);

    var obj = JSON.parse(clientJson);
    clientId = obj["clientId"];

    var file = $('#productFile')[0].files[0];
    readFileData(file, readFileDataCallback);
    return false;
}

function readFileDataCallback(results) {
    fileData = results.data;
    uploadRows();
}

function uploadRows() {
    var formList = [];
    var processCount;
    for (processCount = 0; processCount < fileData.length; processCount++) {
        formList.push(fileData[processCount]);
    }

    validateCsv(formList);
}

function validateCsv(formList) {
    var clientJson = toJson($("#product-form"));
    clientId = JSON.parse(clientJson)["clientId"];
    var url = getProductUrl() + "/validate/" + clientId;

    var json = JSON.stringify(formList);

    $.ajax({
        url: url,
        type: 'POST',
        data: json,
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(response) {
            uploadProductMaster(formList, clientId);
        },
        error: function(response) {
            sessionStorage.setItem("ProductCSVError", JSON.stringify(JSON.parse(response.responseText)['message']));
            errorButtonActivate();
            alert("Errors in CSV");
        }
    });
    return false;
}

function errorButtonActivate() {
    document.getElementById("download-errors").style.visibility = "visible";
    document.getElementById("download-errors").disabled = false;
}

function uploadProductMaster(formList, clientId) {
    var url = getProductUrl() + "/list/" + clientId;
    var json = JSON.stringify(formList);
    $.ajax({
        url: url,
        type: 'POST',
        data: json,
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(response) {
            $('#exampleModal').modal('toggle');
            getSuccessSnackbar("Success");
        },
        error: function(response) {
            alert(response.responseText);
        }
    });
    return false;
}

function downloadErrors() {
    writeFileData(errorData);
}

function resetModal() {
    sessionStorage.setItem("ProductCSVError", '');
    document.getElementById("errorDetails").style.display = "none";
    document.getElementById("download-errors").disabled = true;
    document.getElementById("download-errors").style.visibility = "hidden";
}

function displayProductList(data) {
    var $tbody = $('#product-table').find('tbody');
    $tbody.empty();

    if (data.length == 0) {
        var row = '<tr>' +
            '<td style="text-align:center; font-weight: bold; background-color:#ffebe8;" colspan="8">No Products Created</td>' +
            '</tr>';
        $tbody.append(row);
    }

    for (var i in data) {
        var e = data[i];
        var buttonHtml = '<button class="btn btn-primary btn-sm" onclick="displayEditProduct(' + e.clientId + ',\'' + e.clientSkuId + '\')"><i class="fa fa-wrench" aria-hidden="true"></i></button>'
        var row = '<tr>' +
            '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>' +
            '<td>' + e.clientSkuId + '</td>' +
            '<td>' + e.name + '</td>' +
            '<td>' + e.brandId + '</td>' +
            '<td>' + e.mrp + '</td>' +
            '<td>' + e.description + '</td>' +
            '<td align="right">' + buttonHtml + '</td>' +
            '</tr>';
        $tbody.append(row);
    }
}

function displayEditProduct(clientId, clientSkuId) {
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

function resetUploadDialog() {
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

function updateFileName() {
    var $file = $('#productFile');
    var fileName = $file.val();
    $('#productFileName').html(fileName);
}

function displayUploadData() {
    resetUploadDialog();
    $('#upload-product-modal').modal('toggle');
}

function displayProduct(data) {
    $("#product-edit-form input[name=name]").val(data.name);
    $("#product-edit-form input[name=brandId]").val(data.brandId);
    $("#product-edit-form input[name=mrp]").val(data.mrp);
    $("#product-edit-form input[name=description]").val(data.description);
    $('#edit-product-modal').modal('toggle');
}


function loadClientNames(data) {
    var $clientDropdown = $('#clientId');
    var $clientDisplayDropdown = $('#clientIdSelect');

    for (var clientName in data) {
        var clientId = data[clientName];
        var option = '<option value=' + data[clientName].id + '>' +
            data[clientName].name +
            '</option>';
        $clientDropdown.append(option);
        $clientDisplayDropdown.append(option);
    }
}

function populateDropdown() {
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

function displayProductCsvErrors() {
    document.getElementById('errorDetails').innerHTML = sessionStorage.getItem("ProductCSVError");
    document.getElementById("errorDetails").style.display = "block";
    return false;
}

function init() {
    document.getElementById("product-table").style.visibility = "hidden";
    document.getElementById("refresh-data").disabled = true;
    document.getElementById("download-errors").disabled = true;

    $('#product-edit-form').submit(updateProduct);
    $('#refresh-data').click(getProductListByClient);
    $('#download-errors').click(displayProductCsvErrors);
    $('#upload-data').click(displayUploadData);
    $('#product-form').submit(processData);
    $('#productFile').on('change', updateFileName);
    $('#productDisplayForm').submit(getProductListByClient);
}

$(document).ready(init);
$(document).ready(populateDropdown);