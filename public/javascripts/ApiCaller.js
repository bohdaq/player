var ApiCaller = function() {};

ApiCaller.prototype.deleteAudio = function(id, doneCallback) {
    var url = "/audio/remove/" + id;
    $.post(url).done(doneCallback);
};

ApiCaller.prototype.registerUser = function(data, doneCallback) {
    var url = "/application/register";
    $.post(url, data).done(doneCallback);
};

ApiCaller.prototype.loginUser = function(data, doneCallback) {
    var url = "/application/login";
    $.post(url, data).done(doneCallback);
};

ApiCaller.prototype.createPlaylist = function(name, doneCallback) {
    var url = "/createPlaylist/" + name;
    $.post(url).done(doneCallback);
};

window.apiCaller = new ApiCaller();
