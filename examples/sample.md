# Test Plan: TEST-PLAN

## User Defined Variables

* var1 : value1
* var2 : value2

## ThreadGroup: thread-group-sample-1

* comment : comment for sample.
* users : 1
* ramp-up : 1 sec
* loop : Infinite
* duration : 1 sec
* start delay : 1 sec
* start : delayed
* on error : continue

### Header Manager: Http-Header-Sample

* comment : comment in header
* headers : 
  * Content-Type : application/json; charset=utf-9
  * Set-Cookie : sessionId=xyz987; HttpOnly; Secure

### GET http://10.xxx.xxx.xx1:80/main1?key1=val1&key2=val2

* name : GET-sample
* comment : test get request
* encoding : UTF-8
* follow Redirects : yes
* keep Alive : disabled

### POST http://10.xxx.xxx.xx1:80/main2
#### body

```json
{
  "key1" : "value1",
  "key2" : "value2"
}
```

* name : POST-sample
* comment : test post request
* encoding : UTF-8
* follow Redirects : yes
* keep Alive : enabled

### POST http://10.xxx.xxx.xx1:80/main3
* name : file-upload-sample
* comment : test file upload request
* encoding : UTF-8
* follow Redirects : no
* keep Alive : enabled

#### files
* file : test.txt (text/plain)

### Constant Timer: TImer
* comment : comment for constant timer.
* think time : 2000 ms


