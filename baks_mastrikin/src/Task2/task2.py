def print_results(res):
    pass

results = {'a': 0, 'b': 0, 'c': 0}

for i in ['a', 'b']:
    for j in ['b', 'c']:
        if i != j:
            op = str(input())
            if op == '>':
                results[i] += 1
            elif op == '<':
                results[j] += 1

print_results(results)
