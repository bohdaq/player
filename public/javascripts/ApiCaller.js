var ApiCaller = function() {

};
ApiCaller.prototype.deleteAudio = function(url, doneCallback) {
    $.post(url).done(doneCallback);
};
