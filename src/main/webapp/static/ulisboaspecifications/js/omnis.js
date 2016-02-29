
window.Omnis = {};

(function () {

	/**
		block - Instantiate a blocking modal to prevent user actions

			Arguments:	
					-elementId (string)
					if no id is passed block just ignores that call.

					-messages (object, optional)
					accepts an object with keys 'title' and 'message'
					as configurations for the modal window shown.

			Returns:
					-(anonymous function)
					handler that cancels the blocking modal.

	 */
	this.block = function (elementId, messages) {
		var conf = {};
		if (elementId === undefined) {
			return;
		}
		conf.id = elementId;
		if (messages !== undefined && messages.title) {
			conf.title =  messages.title;
		}
		if (messages !== undefined && messages.message) {
			conf.message =  messages.message;
		}

		var blocker = new Blocker(conf);
		$('#'+elementId).click(function () {blocker.show();});
		return (function () {blocker.hide();});
	};

	function Blocker (conf) {
		var defaultMessages = {
			title: {
				'pt': 'Por favor aguarde&hellip;',
				'en': 'Please wait&hellip;'
			},
			message: {
				'pt': 'A processar o seu pedido.',
				'en': 'Processing your request.'
			}
		};
		var lang = (typeof Bennu !== 'undefined' && Bennu.locale && Bennu.locale.lang) ? Bennu.locale.lang : 'pt';
		this.properties = {
			id: conf.id,
			title: conf.title || defaultMessages.title[lang],
			message: conf.message || defaultMessages.message[lang]
		};
	};
	Blocker.prototype.show = function () {
		$('body').append(
			'<div id="' + this.properties.id + '-blocker" class="modal fade" tabindex="-1" role="dialog">' +
				'<div class="modal-dialog">' +
					'<div class="modal-content">' +
						'<div class="modal-header">' +
							'<h4 class="modal-title">' + this.properties.title + '</h4>' +
						'</div>' +
						'<div class="modal-body">' +
							'<p>' + this.properties.message + '</p>' +
							'<div class="turning-gears"><span class="glyphicon glyphicon-cog bigcog"></span><span class="glyphicon glyphicon-cog smallcog"></span></div>' +
						'</div>' +
					'</div>' +
				'</div>' +
			'</div>'
		);
		$('#' + this.properties.id + '-blocker').modal({
			backdrop: 'static',
			keyboard: false,
			show: true
		});
	};
	Blocker.prototype.hide = function () {
		$('#' + this.properties.id + '-blocker').modal('hide');
	};

}).apply(window.Omnis);

function messageAlert(title, message) {
	bootbox.dialog({
		title : title,
		message : message,
		buttons : {
			success : {
				label : "Ok",
				className : "btn-primary",
			}
		}
	});
}

window.jQueryClosures = []

function initSelect2(element_id, elements_data_source, element_init_val) {
	var func = function() {
		$(element_id).select2({
			data : elements_data_source
		}).select2('val', element_init_val);
	};
	window.jQueryClosures.push(func);
}

function initSelect2Multiple(element_id, elements_data_source, element_init_values) {

	var func = function() {
		var select2 = $(element_id).select2({
			data : elements_data_source
		});
		if (element_init_values != undefined)
		{
			select2.select2('val', element_init_values);
		}
	};
	window.jQueryClosures.push(func);
}


$(document).ready(function() {
	for (var i = 0; i < window.jQueryClosures.length; i++) {
		window.jQueryClosures[i].call();
	}
});

function createAngularPostbackFunction(angular_scope) {
	return function(model) {

		angular_scope.$apply();
		var form = $('form[name="' + angular_scope.form.$name + '"]');
		var previousActionURL = form.attr("action");
		form.submit = function(e) {
			var postData = $(this).serializeArray();
			var formURL = $(this).attr("action");
			$.ajax({
				url : formURL,
				type : "POST",
				data : postData,
				success : function(data, textStatus, jqXHR) {
					angular_scope.object = data;
					angular_scope.$apply();
				},
				error : function(jqXHR, textStatus, errorThrown) {
					messageAlert("Erro", jqXHR.responseText);
				},
			});
		};

		form.attr("action", form.find('input[name="postback"]').attr('value'));
		form.submit();
		form.attr("action", previousActionURL);
	};
}

function createDataTables(tableid, showsearchbox, showtools,pagination, pagecontext,i18nurl) {
	var dom = "";
	if (showsearchbox == true && showtools == true) {
		dom = '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip'; //FilterBox = YES && ExportOptions = YES
	} else if (showsearchbox == true && showtools == false) {
		dom = '<"col-sm-6"l><"col-sm-6"f>rtip'; // FilterBox = YES && ExportOptions = NO
	} else if (showsearchbox == false && showtools == true) {
		dom = 'T<"clear">lrtip'; // FilterBox = NO && ExportOptions = YES
	} else {
		dom = '<"col-sm-6"l>rtip'; // FilterBox = NO && ExportOptions = NO
	}
	var table = $('#'+tableid)
			.DataTable({language : {
				url : i18nurl,			
			},
			"bDeferRender" : true,
			"bPaginate" : pagination,
			"dom" : dom, 
			"tableTools" : {
				"sSwfPath" : pagecontext + "/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"
			},
			"lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, "All"] ]
	});
	table.columns.adjust().draw();
	$('#' + tableid +' tbody').on('click', 'tr', function() {
		$(this).toggleClass('selected');
	});
}