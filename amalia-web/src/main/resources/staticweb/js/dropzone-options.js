Dropzone.options.myDropzone = {
	createImageThumbnails: false,
	maxFilesize: 10, // MB
	queuecomplete: function() {
		window.location.reload(false);
	},
	error: function(file) {
		alert('Upload hat nicht geklappt!');
	},
};
