<? header('Access-Control-Allow-Origin: *');
	header('Content-Type: text/html');  
	header("Access-Control-Allow-Methods: POST, GET");
	header("Access-Control-Allow-Headers: x-requested-with"); 
?>
<div id="{{codigo}}" class="articulo {{abierto}}">
	<div class="imagen {{video}}" data-imagen="{{imagen}}" style="background-image: url({{imagePath}})">
    	<div class="imageButton"></div>
    </div>
    <p class="volanta">{{volanta}}</p>
    <p class="titular">{{titular}}</p>
    <div class='mm'></div>
    <div class="bajada">{{bajada}}</div>
    <div class="cuerpo">{{cuerpo}}</div>
</div>
    