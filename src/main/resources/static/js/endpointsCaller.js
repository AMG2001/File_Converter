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
    // Create an input element dynamically
    var input = document.createElement('input');
    input.type = 'file';
    input.accept = '.pdf'; // Set the accept attribute to .pdf

    // Add an event listener to handle the file selection
    input.onchange = function (event) {
        var file = event.target.files[0];
        var formData = new FormData();
        formData.append('file', file);
        formData.append('filename', file.name); // Include the original filename
        $.ajax({
            url: 'http://localhost:8080/convertPdfToImages',
            type: 'POST',
            data: formData,
            processData: false, // tell jQuery not to process the data
            contentType: false, // tell jQuery not to set contentType
            // Make sure to set the responseType to 'blob'
            xhrFields: {
                responseType: 'blob'
            },
            success: function (data) {
                // Create a Blob from the zip bytes
                var blob = new Blob([data], {type: 'application/zip'});
                // Create a URL from the Blob
                var url = URL.createObjectURL(blob);
                // Create a link element
                var link = document.createElement('a');
                link.href = url;
                // Set the download attribute to the desired file name
                link.download = file.name.replace('.pdf', '.zip');
                // Append the link to the body
                document.body.appendChild(link);
                // Trigger the click event on the link
                link.click();
                // Remove the link from the body
                document.body.removeChild(link);
            }
        });
    };

    // Trigger the file dialog programmatically
    input.click();
}
