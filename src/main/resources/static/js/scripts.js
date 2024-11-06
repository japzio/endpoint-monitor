$(document).ready(
    function(){
        $("#submitbutton").click(
            function(){

                $.get("/v1/status",
                    function(response,status){
                      console.log(typeof response)
                      console.log(JSON.stringify(response));
                      refreshTable(response);
                    });

            });

    }
);

function refreshTable(data) {
    const personsObject = JSON.parse(JSON.stringify(data));
    const personsMap = new Map(Object.entries(personsObject));
    var html = "";
    html += "<table>";
    for (const key of personsMap.keys()) {
        html += "<tr><td>";
        html += JSON.stringify(key);
        html += "</td><td>";
        html += JSON.stringify(personsMap.get(key));
        console.log(key)
        html += "</td></tr>";
    }
    html += "</table>";
    console.log(html);
    $("#container4").html(html);

}
