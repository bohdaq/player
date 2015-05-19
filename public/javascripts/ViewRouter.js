var ViewRouter = function() {
    ViewRouter.previousViewId =  window.location.hash.substring(1) || "listOfAudios";
    this.showView(ViewRouter.previousViewId);
};
ViewRouter.prototype.routeTo = function(idOfElement) {
    this.hideView(ViewRouter.previousViewId);
    this.showView(idOfElement);
    window.history.pushState(idOfElement, idOfElement, "/#" + idOfElement);
};
ViewRouter.prototype.showView = function(idOfElement){
    document.getElementById(idOfElement).style.display = "block";
    console.log("Shown " + idOfElement);
}
ViewRouter.prototype.hideView = function(idOfElement){
    document.getElementById(idOfElement).style.display = "none";
    console.log("Hide " + idOfElement);
}