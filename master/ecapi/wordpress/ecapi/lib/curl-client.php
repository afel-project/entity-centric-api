<?php/** * REST client methods using cURL. * * @author Alessandro Adamou <alexdma@apache.org> *//** * @return mixed * 		the HTTP response, either structured as an array (default) or raw if its corresponsing option is set. */function doCurl( $endpoint, $method = 'GET', $data = NULL, $options = array(), $proxy_config = array() ) {	$ch = curl_init($endpoint);		// TODO check proxy		switch( $method ) {		case 'GET':			curl_setopt($ch, CURLOPT_HTTPGET, true);			break;		case 'POST':			curl_setopt($ch, CURLOPT_POST, 1);			if( $data ) curl_setopt($ch, CURLOPT_POSTFIELDS, $data);			break;		default:			curl_setopt($ch, CURLOPT_HTTPGET, true);	}		// Set to true once libcurl returns the correct header size.	curl_setopt($ch, CURLOPT_HEADER, false);	curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);	curl_setopt($ch, CURLOPT_RETURNTRANSFER , true);	if( isset($options['accept']) )		curl_setopt( $ch, CURLOPT_HTTPHEADER, array (			"Accept: {$options['accept']}"		) );		// No SSL service worked without this so far.	if( preg_match( "/^https:\/\/(.*)$/", $endpoint ) ) 		curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);		$response = curl_exec($ch);	if( !empty($options["raw"]) && $options["raw"] ) return $response;	$error = curl_error($ch);	$result = array( 		'header' => '',		'body' => '',		'curl_error' => '',		'http_code' => '',		'last_url' => '');	if ( $error != "" ) {		$result['curl_error'] = $error;	} else {		/*		 * Proxy + returntransfer and other cURL options seem 		 * to cause CURLINFO_HEADER_SIZE to be wrong.		 * Do not print header until this is fixed in libcurl.		 * See https://bugs.php.net/bug.php?id=63894		 */		// $header_size = curl_getinfo($ch, CURLINFO_HEADER_SIZE);		// $result['header'] = substr($response, 0, $header_size);		$result['body'] = $response; // substr( $response, $header_size );		$result['http_code'] = curl_getinfo($ch, CURLINFO_HTTP_CODE);		$result['content_type'] = curl_getinfo($ch, CURLINFO_CONTENT_TYPE);		$result['last_url'] = curl_getinfo($ch, CURLINFO_EFFECTIVE_URL);	}	curl_close($ch);		return $result;}