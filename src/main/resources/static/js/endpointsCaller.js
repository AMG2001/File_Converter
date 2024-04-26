function docxToPdfConverter() {
    console.log("docxToPdfConverter Called");
    // Create an input element dynamically
    var input = document.createElement('input');
    input.type = 'file';
    input.accept = '.docx';  // Set the accept attribute to .docx

    // Add an event listener to handle the file selection
    input.onchange = function (event) {
        var file = event.target.files[0];
        var formData = new FormData();
        formData.append('file', file);
        formData.append('filename', file.name);  // Include the original filename
        $.ajax({
            url: 'http://localhost:8080/convertDocxToPdf',
            type: 'POST',
            data: formData,
            processData: false,  // tell jQuery not to process the data
            contentType: false,  // tell jQuery not to set contentType
            // Make sure to set the responseType to 'blob'
            xhrFields: {
                responseType: 'blob'
            },
            success: function (data) {
                // Create a Blob from the PDF bytes
                var blob = new Blob([data], {type: 'application/pdf'});
                // Create a URL from the Blob
                var url = URL.createObjectURL(blob);
                // Open the PDF in a new window
                window.open(url, '_blank');
            }
        });
    };

    // Trigger the file dialog programmatically
    input.click();
}

function pdfToImagesConverter() {
    console.log("Pdf to images Called");
    var input = document.createElement('input');
    input.type = 'file';
    input.accept = '.pdf';

    input.onchange = function (event) {
        var file = event.target.files[0];
        var formData = new FormData();
        formData.append('file', file);
        formData.append('filename', file.name);

        // Update the text to "Uploading..."
        document.getElementById('txt_pdfToImages').textContent = 'Uploading...';

        $.ajax({
            url: 'http://localhost:8080/convertPdfToImages',
            type: 'POST',
            data: formData,
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
                        document.getElementById('txt_pdfToImages').textContent = 'Uploading: ' + percentComplete.toFixed(2) + '%';
                        if (!isNaN(Number(percentComplete)) && Number(percentComplete) === 100) {
                            document.getElementById('txt_pdfToImages').textContent = 'processing';
                        }
                    }
                }, false);
                return xhr;
            },
            success: function (data) {
                // Update the text to "Processing..."
                document.getElementById('txt_pdfToImages').textContent = 'Processing...';

                var blob = new Blob([data], {type: 'application/zip'});
                var url = URL.createObjectURL(blob);
                var link = document.createElement('a');
                link.href = url;
                link.download = file.name.replace('.pdf', '.zip');
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
                // Update the text to "Downloading..."
                document.getElementById('txt_pdfToImages').textContent = 'Downloading...';

            }
        });
    };

    input.click();
}


function convertImagesToPdf() {
    console.log("Images to PDF Called");
    var input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*'; // Accept all image types
    input.multiple = true; // Allow multiple files to be selected

    input.onchange = function (event) {
        var files = event.target.files;
        var formData = new FormData();

        // Append each file to the form data
        for (var i = 0; i < files.length; i++) {
            formData.append('files', files[i]);
        }

        // Update the text to "Uploading..."
        document.getElementById('txt_imagesToPdf').textContent = 'Uploading...';

        $.ajax({
            url: 'http://localhost:8080/convertImagesToPdf', // Adjust the URL to your server endpoint
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
                            document.getElementById('txt_imagesToPdf').textContent = 'processing';
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