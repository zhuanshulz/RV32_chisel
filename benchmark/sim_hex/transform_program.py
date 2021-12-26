import os

RV_ROOT=os.getenv('RV_ROOT')
assert RV_ROOT

programordata='program'

input_file=open(RV_ROOT + '/benchmark/sim_hex/' +programordata+'.hex')


output_file1=open(RV_ROOT + '/benchmark/sim_hex/'+programordata+'_iccm' + '_1.hex','w')
output_file2=open(RV_ROOT + '/benchmark/sim_hex/'+programordata+'_iccm' + '_2.hex','w')
output_file3=open(RV_ROOT + '/benchmark/sim_hex/'+programordata+'_iccm' + '_3.hex','w')
output_file4=open(RV_ROOT + '/benchmark/sim_hex/'+programordata+'_iccm' + '_4.hex','w')
# output_file5=open(RV_ROOT + '/benchmark/sim_hex/'+programordata+'5.hex','w')
# output_file6=open(RV_ROOT + '/benchmark/sim_hex/'+programordata+'6.hex','w')
# output_file7=open(RV_ROOT + '/benchmark/sim_hex/'+programordata+'7.hex','w')
# output_file8=open(RV_ROOT + '/benchmark/sim_hex/'+programordata+'8.hex','w')

addr=0
i=0
instr=''

while True:
    line=input_file.readline()
    if not line: break
    line_list=line.split()
    if line_list[0][0]=='@':
        addr_local=int(line_list[0][5:],16)
        if addr_local == addr + i :
            continue
        elif i!=0:
            if (addr)%4==0:
                output_file1.write('@'+hex(addr//4)[2:].upper())
                output_file1.write('\n')
                output_file1.write(instr)
                output_file1.write('\n')
            elif (addr)%4==1:
                output_file2.write('@'+hex(addr//4)[2:].upper())
                output_file2.write('\n')
                output_file2.write(instr)
                output_file2.write('\n')
            elif (addr)%4==2:
                output_file3.write('@'+hex(addr//4)[2:].upper())
                output_file3.write('\n')
                output_file3.write(instr)
                output_file3.write('\n')
            elif (addr)%4==3:
                output_file4.write('@'+hex(addr//4)[2:].upper())
                output_file4.write('\n')
                output_file4.write(instr)
                output_file4.write('\n')
            # elif (addr/4)%8==4:
            #     output_file5.write('@'+hex(addr/32)[2:].upper())
            #     output_file5.write('\n')
            #     output_file5.write(instr)
            #     output_file5.write('\n')
            # elif (addr/4)%8==5:
            #     output_file6.write('@'+hex(addr/32)[2:].upper())
            #     output_file6.write('\n')
            #     output_file6.write(instr)
            #     output_file6.write('\n')
            # elif (addr/4)%8==6:
            #     output_file7.write('@'+hex(addr/32)[2:].upper())
            #     output_file7.write('\n')
            #     output_file7.write(instr)
            #     output_file7.write('\n')
            # elif (addr/4)%8==7:
            #     output_file8.write('@'+hex(addr/32)[2:].upper())
            #     output_file8.write('\n')
            #     output_file8.write(instr)
            #     output_file8.write('\n')
            instr=''
            i=0
        addr = addr_local
    else:
        for str in line_list:
            instr=str+instr
            i+=1
            if (addr)%4==0:
                output_file1.write('@'+hex(addr//4)[2:].upper())
                output_file1.write('\n')
                output_file1.write(instr)
                output_file1.write('\n')
            elif (addr)%4==1:
                output_file2.write('@'+hex(addr//4)[2:].upper())
                output_file2.write('\n')
                output_file2.write(instr)
                output_file2.write('\n')
            elif (addr)%4==2:
                output_file3.write('@'+hex(addr//4)[2:].upper())
                output_file3.write('\n')
                output_file3.write(instr)
                output_file3.write('\n')
            elif (addr)%4==3:
                output_file4.write('@'+hex(addr//4)[2:].upper())
                output_file4.write('\n')
                output_file4.write(instr)
                output_file4.write('\n')
                # elif (addr/4)%8==4:
                #     output_file5.write('@'+hex(addr/32)[2:].upper())
                #     output_file5.write('\n')
                #     output_file5.write(instr)
                #     output_file5.write('\n')
                # elif (addr/4)%8==5:
                #     output_file6.write('@'+hex(addr/32)[2:].upper())
                #     output_file6.write('\n')
                #     output_file6.write(instr)
                #     output_file6.write('\n')
                # elif (addr/4)%8==6:
                #     output_file7.write('@'+hex(addr/32)[2:].upper())
                #     output_file7.write('\n')
                #     output_file7.write(instr)
                #     output_file7.write('\n')
                # elif (addr/4)%8==7:
                #     output_file8.write('@'+hex(addr/32)[2:].upper())
                #     output_file8.write('\n')
                #     output_file8.write(instr)
                #     output_file8.write('\n')
            instr=''
            addr+=i
            i=0
if i!=0:
    if (addr)%4==0:
        output_file1.write('@'+hex(addr//4)[2:].upper())
        output_file1.write('\n')
        output_file1.write(instr)
        output_file1.write('\n')
    elif (addr)%4==1:
        output_file1.write('@'+hex(addr//4)[2:].upper())
        output_file1.write('\n')
        output_file1.write(instr)
        output_file1.write('\n')
    elif (addr)%4==2:
        output_file1.write('@'+hex(addr//4)[2:].upper())
        output_file1.write('\n')
        output_file1.write(instr)
        output_file1.write('\n')
    elif (addr)%4==3:
        output_file1.write('@'+hex(addr//4)[2:].upper())
        output_file1.write('\n')
        output_file1.write(instr)
        output_file1.write('\n')
    # elif (addr/4)%8==4:
    #     output_file5.write('@'+hex(addr/32)[2:].upper())
    #     output_file5.write('\n')
    #     output_file5.write(instr)
    #     output_file5.write('\n')
    # elif (addr/4)%8==5:
    #     output_file6.write('@'+hex(addr/32)[2:].upper())
    #     output_file6.write('\n')
    #     output_file6.write(instr)
    #     output_file6.write('\n')
    # elif (addr/4)%8==6:
    #     output_file7.write('@'+hex(addr/32)[2:].upper())
    #     output_file7.write('\n')
    #     output_file7.write(instr)
    #     output_file7.write('\n')
    # elif (addr/4)%8==7:
    #     output_file8.write('@'+hex(addr/32)[2:].upper())
    #     output_file8.write('\n')
    #     output_file8.write(instr)
    #     output_file8.write('\n')
    instr=''
    addr+=i
    i=0
            


input_file.close()

output_file1.close()
output_file2.close()
output_file3.close()
output_file4.close()
# output_file5.close()
# output_file6.close()
# output_file7.close()
# output_file8.close()


