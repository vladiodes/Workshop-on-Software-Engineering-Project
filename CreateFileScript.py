#to create csv files easy
def write_to_file(filename,p):
    with open(filename, 'w') as f:
        for i in range(100):
            f.write(p + str(99 - i) +'\n')
write_to_file('Stores.csv',"u")
