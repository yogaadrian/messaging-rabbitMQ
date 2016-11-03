Desain Aplikasi
Aplikasi terdiri dari 2 komponen yang terprogram
	1. Messenger Client
	2. Messenger Server
Dalam komunikasi antar 2 komponen tersebut, digunakan RabbitMQ sebagai Message Broker. Setiap komponen memiliki message queue sendiri dengan id dirinya sendiri. Hal tersebut juga berlaku untuk server. Untuk komunikasi group, digunakan "exchange" untuk mengirim pesan ke semua queue yang terdaftar dalam exchange. Exchange tersebut diketahui oleh server. Segala komunikasi yang dilakukan (private chat atau group chat), pasti melewati server messenger. 
Untuk representasi group, user disimpan dalam database. Setiap program server di jalankan, data dari database akan diambil dan direpresentasikan lagi ke exchange, queue dan lain lain. Perintah khusus seperti membuat group, leave group, di kirimkan ke server oleh client dan di proses di server. Untuk menjalankan aplikasi ini, dibutuhkan 1 node yang terinstall rabbitmq server.

Petunjuk Instalasi
1. Buka 2 projek yang bersesuaian (Client dan Server)
2. Lakukan Build kepada 2 projek tersebut.
3. Ambil 1 file server.jar , folder lib, file database dan letakkan dalam 1 folder
4. Ambil 1 file client.jar , folder lib dan letakkan dalam 1 folder

Cara Menjalankan Program 
1. Jalankan kedua file.jar dari hasil build (instalasi).
	file .jar dijalankan dengan cara
		- masuk ke terminal
		- masuk ke path file .jar
		- jalankan "java -jar server.jar" untuk server, dan "java -jar client.jar" untul client
2. Masukkan alamat dan port dari node yang terinstall rabiitmq server		
3. Masukkan command pada aplikasi client messenger
4. Tekan help untuk mendapatkan list command.

Tes
1. Help(Fungsi tambahan)
	>> help

	'register' untuk mendaftarkan user baru
	'login' untuk masuk ke aplikasi dengan user yang sudah terdaftar
	command dibawah ini memerlukan login terlebih dahulu
	'create-group' untuk mendaftarkan grup baru
	'leave-group' untuk meninggalkan group yang diikuti
	'add-friend' untuk menambahkan teman baru
	'chat-group' untuk chat ke group yang terdaftar
	'chat-friend' untuk chat ke teman yang terdaftar
	'get-friends' untuk mendapatkan list teman yang terdaftar
	'get-groups' untuk mendaftarkan list group yang terdaftar

2. Tanpa Login
	>>get-friends
	Perlu melakukan login
	>>get-groups
	Perlu melakukan login
	>>create-group
	Perlu melakukan login
	>>leave-group
	Perlu melakukan login
	>>add-friend
	Perlu melakukan login
	>>chat-group
	Perlu melakukan login
	>>chat-friend
	Perlu melakukan login
	
3. Register
	>> register
	Masukkan userid : budi
	Masukkan password : 123
	berhasil

	>> register (ulang)
	Masukkan userid : budi
	Masukkan password : 123
	gagal
	
4. Login
	>>login (password salah)
	Masukkan userid : budi
	Masukkan password : 124
	gagal
	
	>>login (password dan user benar)
	Masukkan userid : budi
	Masukkan password : 123
	berhasil

	>>login (user id salah)
	Masukkan userid : tito
	Masukkan password : 123
	gagal
	
5. List Friends and List Group
	>>login
	Masukkan userid : yoga
	Masukkan password : 123
	berhasil
	
	>>get-friends
	[List Friend]
	
	>>get-groups
	[List Group]
	t1
	test
	a
	
6. Leave Group
	>>login
	Masukkan userid : yoga
	Masukkan password : 123
	berhasil
	>>leave-group
	Masukkan nama grup : test
	leave group NULL
	
7. Add Friend
	(kasus berhasil)
	>>login
	Masukkan userid : yoga
	Masukkan password : 123
	berhasil
	>>add-friend
	Masukkan nama teman : fiqie
	addfriend fiqie

	>>login
	Masukkan userid : fiqie
	Masukkan password : 123
	berhasil
	addfriend yoga

	(kasus sudah berteman)
	>>login
	Masukkan userid : yoga
	Masukkan password : 123
	berhasil
	>>add-friend
	Masukkan nama teman : fiqie
	sudah menjadi teman

	>>chat-friend
	Masukkan nama teman : budi
	tidak ada teman itu

8.	Chat private	
	--kondisi teman terdaftar
	>>login
	Masukkan userid : yoga
	Masukkan password : 123
	berhasil
	>>chat-friend
	Masukkan nama teman : fiqie
	wowow
	<client lain>
	>>login
	Masukkan userid : fiqie
	Masukkan password : 123
	berhasil
	yoga : wowow

	--kondisi teman tidak terdaftar
	>>login
	Masukkan userid : fiqie
	Masukkan password : 123
	>>get-friends
	[List Friend]
	yoga
	>>chat-friend
	Masukkan nama teman : tomcruise
	tidak ada teman itu

9. 	create group
	>>login
	Masukkan userid : yoga
	Masukkan password : 123
	berhasil
	>>create-group
	Masukkan nama grup : a
	Masukkan user id member dengan menekan enter setelah setiap user id. masukkan -1 untuk berhenti
	fiqie
	-1
	join group a
	success
	>>get-groups
	[List Group]
	a 

	<client lain>
	>>login
	Masukkan userid : fiqie
	Masukkan password : 123
	berhasil
	yoga : wow
	join group a
	>>get-groups
	[List Group]
	a

	--kondisi teman tidak terdaftar
	>>login
	Masukkan userid : yoga
	Masukkan password : 123
	berhasil
	>>create-group
	Masukkan nama grup : test
	Masukkan user id member dengan menekan enter setelah setiap user id. masukkan -1 untuk berhenti
	tomcruise
	-1
	join group test
	success tomcruise not a user! 
	--group terbentuk, tapi user yang tidak terdaftar tidak dimasukkan

	--kondisi sudah terdaftar dalam group
	>>get-groups
	[List Group]
	a
	test
	>>create-group
	Masukkan nama grup : test
	sudah terdaftar dalam group

	--kondisi sudah ada nama group tersebut
	<client lain>
	>>get-groups
	[List Group]
	a
	>>create-group
	Masukkan nama grup : test
	Masukkan user id member dengan menekan enter setelah setiap user id. masukkan -1 untuk berhenti
	-1
	>>Group already exist

10.	chat-group
	--kondisi sama terdaftar dalam group
	>>login
	Masukkan userid : yoga
	Masukkan password : 123
	berhasil
	>>chat-group
	Masukkan nama group : a
	coba
	a , yoga : coba

	<client lain>
	>>login
	Masukkan userid : fiqie
	Masukkan password : 123
	berhasil
	a , yoga : coba

	--kondisi beda group
	>>get-groups
	[List Group]
	a
	test
	>>chat-group
	Masukkan nama group : test
	Masukkan pesan : auoo
	test , yoga : auoo

	<beda client>
	>>get-groups
	[List Group]
	a
	--tidak ada pesan masuk

11.	add user to group
	>>add-user-to-group
	Masukkan nama grup : test
	Masukkan user id : fiqie
	success

	<client lain>
	>>login
	Masukkan userid : fiqie
	Masukkan password : 123
	berhasil
	join group test
	>>get-groups
	[List Group]
	a
	test


Langkah-langkah melakukan tes
1. Jalankan 2 program(client messenger dan server messenger)
2. Jalankan rabbitmq server pada localhost. input alamat localhost
3. Masukkan command yang diinginkan
4. Coba segala skenario yang memungkinkan (skenario benar dan salah, atau konten pesan yang salah maupun benar)
5. Cek database untuk command yang bersesuaian.
6. Lihat hasilnya dan bandingkan dengan yang seharusnya.
