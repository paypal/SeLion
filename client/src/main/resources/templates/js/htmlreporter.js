	$(document).ready(function(){ 
	
		var iframe = $('iframe#currentFrameId');
		
		// logic to show and add the tabs.
		$('.tab').click(function(event){
			$(this).parent().children().removeClass('active');;
			$(this).addClass('active');
			var tabId= $(this).attr('id');
			$('div.tabContentContainer').hide();
			$('div#container_'+tabId).show();
		});
		
		// by default load the first visible one.
		$('.tab:first').click();
		
		
		// when a clickable item is clicked, propage that click to the element with the same id on the 
		// page ( the same method could be in several tabs ) and to the server.
		$('.clickable').click(function(event){
			var content_id= $(this).attr('content_id');
			
			if ("NI" == content_id ){
				return;
			}

			$('td.first').each(function(index) {
    			if ($(this).attr('content_id') == content_id){
    				$(this).toggleClass('clickedOnce');	
    			}
  			});
			
			// display the result in the iframe at the bottom of the page
			iframe.attr('src', content_id + '.html');
		});
	  });