import boto3

client = boto3.client(
    'dynamodb',
    aws_access_key_id = "",
    aws_secret_access_key="",
)

# Read in lines from file:
filepath = "sql_dump.txt"
with open(filepath) as f:
    sql_dump_table = f.readlines()

sql_dump_table = [line.split("|") for line in sql_dump_table]
# Example row
#['', '    2 ', ' +11111111  ', ' eng      ', ' complete           ', '\n']

# Select the items in each row we want and remove all white spaces
sql_dump_table = [[line[1].strip(), line[2].strip(), line[3].strip(), line[4].strip()] for line in sql_dump_table]


for id, phone_number, language, state in sql_dump_table:
    client.put_item(
        TableName = "sms-alert-sys",
        Item={
            'id': {"N": id},
            'phone_number': {"S": phone_number},
            'language': {"S": language},
            'state': {"S": state},
            'zip_code': {"N": "0"},
            }
    )
