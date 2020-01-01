//Returns the URL to call the APIs
function getConsumerApiUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/consumer";
}

//Add a consumer
function addConsumer(event){
	var $form = $("#consumer-form");
	var json = toJson($form);
	var url = getConsumerApiUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getConsumerList();
	   },
	   error: handleAjaxError
	});
	return false;
}

//GET Method: Retrieve all Consumers
function getConsumerList(){
	var url = getConsumerApiUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayConsumerList(data);
	   },
	   error: handleAjaxError
	});
}

//Display table of Consumers
function displayConsumerList(data){
	var $tbody = $('#consumer-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var row = '<tr>'
		+ '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>'
		+ '<td>' + e.name + '</td>'
		+ '<td>' + e.type + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

//Initialization Code
function init(){
	$('#add-consumer').click(addConsumer);
	$('#refresh-data').click(getConsumerList);
}

$(document).ready(init);
$(document).ready(getConsumerList);

