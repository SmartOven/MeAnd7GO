N = str(int(input()))
i = len(N) - 1
while N[i] == '0':
    i -= 1
print(N[:i + 1].count('0'))
