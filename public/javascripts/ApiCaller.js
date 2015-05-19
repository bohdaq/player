var ApiCaller = function() {};

ApiCaller.prototype.deleteAudio = function(url, doneCallback) {
    $.post(url).done(doneCallback);
};

ApiCaller.prototype.registerUser = function(url, data, doneCallback) {
    $.post(url, data).done(doneCallback);
};

ApiCaller.prototype.loginUser = function(url, data, doneCallback) {
    $.post(url, data).done(doneCallback);
};

window.apiCaller = new ApiCaller();
