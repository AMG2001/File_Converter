function convertEPUB_to_PDF() {
    console.log("EPUB to PDF Called");
    var input = document.createElement('input');
    input.type = 'file';
    input.accept = 'application/epub+zip'; // Accept EPUB files
    input.multiple = false; // Allow only one file to be selected

    input.onchange = function (event) {
        var files = event.target.files;
        var formData = new FormData();

        // Append the file to the form data
        formData.append('file', files[0]); // Assuming only one file is selected

        // Update the text to "Uploading..."
        document.getElementById('txt_imagesToPdf').textContent = 'Uploading...';

        $.ajax({
            url: 'http://localhost:8080/convertEPUBToPDF', // Adjust the URL to your server endpoint for EPUB to PDF conversion
            type: 'POST',
            data: formData,
            timeout: 60000, // Timeout set to 60 seconds
            processData: false,
            contentType: false,
            xhrFields: {
                responseType: 'blob'
            },
            xhr: function () {
                var xhr = new window.XMLHttpRequest();
                xhr.upload.addEventListener('progress', function (evt) {
                    if (evt.lengthComputable) {
                        var percentComplete = (evt.loaded / evt.total) * 100;
                        // Update the text to show the upload progress
                        document.getElementById('txt_imagesToPdf').textContent = 'Uploading: ' + percentComplete.toFixed(2) + '%';
                        if (!isNaN(Number(percentComplete)) && Number(percentComplete) === 100) {
                            document.getElementById('txt_imagesToPdf').textContent = 'Processing';
                        }
                    }
                }, false);
                return xhr;
            },
            success: function (data) {
                // Update the text to "Processing..."
                document.getElementById('txt_imagesToPdf').textContent = 'Processing...';

                var blob = new Blob([data], {type: 'application/pdf'});
                var url = URL.createObjectURL(blob);
                var link = document.createElement('a');
                link.href = url;
                link.download = 'converted.pdf'; // Set a default name for the PDF
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);

                // Update the text to "Downloading..."
                document.getElementById('txt_imagesToPdf').textContent = 'Downloading...';
            }
        });
    };

    input.click();
}
