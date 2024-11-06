
$(document).ready(function(){
  $("button").click(function(){
    $.get("/v1/status",
    function(data,status){
      alert(
        "Status: " + status + "\nData: " + JSON.stringify(data)
      );
    });
  });
});
