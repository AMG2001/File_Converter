

function imageToTextConverter() {
    console.log("Pdf to images Called");
    var input = document.createElement('input');
    input.type = 'file';
    input.accept = '.png,.jpeg,.jpg';

    input.onchange = function (event) {
        var file = event.target.files[0];
        var formData = new FormData();
        formData.append('image', file);

        // Update the text to "Uploading..."
        document.getElementById('txt_imageToText').textContent = 'Uploading...';

        $.ajax({
            url: 'http://localhost:8080/convertImageToText',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            xhrFields: {
                responseType: 'string'
            },
            xhr: function () {
                var xhr = new window.XMLHttpRequest();
                xhr.upload.addEventListener('progress', function (evt) {
                    if (evt.lengthComputable) {
                        var percentComplete = (evt.loaded / evt.total) * 100;
                        // Update the text to show the upload progress
                        document.getElementById('txt_imageToText').textContent = 'Uploading: ' + percentComplete.toFixed(2) + '%';
                        if (!isNaN(Number(percentComplete)) && Number(percentComplete) === 100) {
                            document.getElementById('txt_imageToText').textContent = 'processing';
                        }
                    }
                }, false);
                return xhr;
            },
            success: function (data) {
                // Update the text to "Processing..."
                document.getElementById('txt_imageToText').textContent = data;
            }
        });
    };

    input.click();
}
