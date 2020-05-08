window.onload = function(){
	var passwordInput = document.getElementById("password");
	passwordInput.addEventListener("keyup", function(e){
		if(e.keyCode == 13){
			document.getElementById("login")[0].click()
		}
	})
}
