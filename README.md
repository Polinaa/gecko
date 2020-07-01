#WHAT
REST interface to consume data-snapshots from one client, validate
and persist data in storage, distribute persisted data to other clients via REST interface.

###File structure
- First line of file will contain header:
PRIMARY_KEY,NAME,DESCRIPTION,UPDATED_TIMESTAMP
- Last line of file always to be empty
- All other lines will contain four values what represents single record to be
persisted
    
*UPDATED_TIMESTAMP is expected to be received as timestamp in milliseconds: 1593505253000

###API

#### POST /data
    file: MultipartFile *required
#### GET /data
    from: time in ISO date time format 2020-07-01T13:15:30Z
    to:   time in ISO date time format 2020-07-01T13:15:30Z
    size: int
#### GET /data/{key}

#### DELETE /data/{key}

