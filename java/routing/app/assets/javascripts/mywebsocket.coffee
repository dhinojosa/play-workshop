connection = new WebSocket('ws://localhost:9000/ws')

connection.onerror = (error) ->
  console.log ( 'WebSocket Error ' + error )

connection.onmessage = (e) ->
  console.log ( 'Server: ' + e.data );
  $('#server_msg').append('<p>' + e.data + '</p>')
