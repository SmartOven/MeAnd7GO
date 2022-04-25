op1 = str(input())
op2 = str(input())
op3 = str(input())
if op1 == '<' and op2 == '<' and op3 == '<':
    print("abc")

if op1 == '<' and op2 == '<' and op3 == '>':
    print("acb")

if op1 == '<' and op2 == '<' and op3 == '=':
    print("abc")
    print("acb")

if op1 == '<' and op2 == '>' and op3 == '>':
    print("cab")

if op1 == '<' and op2 == '=' and op3 == '>':
    print("acb")
    print("cab")

if op1 == '>' and op2 == '<' and op3 == '<':
    print("bac")

if op1 == '>' and op2 == '>' and op3 == '<':
    print("bca")

if op1 == '>' and op2 == '>' and op3 == '>':
    print("cba")

if op1 == '>' and op2 == '>' and op3 == '=':
    print("bca")
    print("cba")

if op1 == '>' and op2 == '=' and op3 == '<':
    print("bac")
    print("bca")

if op1 == '=' and op2 == '<' and op3 == '<':
    print("abc")
    print("bac")

if op1 == '=' and op2 == '>' and op3 == '>':
    print("cab")
    print("cba")

if op1 == '=' and op2 == '=' and op3 == '=':
    print("abc")
    print("acb")
    print("bac")
    print("bca")
    print("cab")
    print("cba")
