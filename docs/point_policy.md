# Point Policy 

## 1. Point Recharge Policy

### Sequence Diagram
![point_recharge_sequence_diagram.png](point_recharge_sequence_diagram.png)

- 포인트 최대 잔고는 `100만` 포인트다. 
- 포인트 충전 후 최대 잔고를 초과하는 경우, 포인트 충전 요청이 실패한다.  

## 2. Point Usage Policy

### Sequence Diagram
![point_usage_sequence_diagram.png](point_usage_sequence_diagram.png)

- 가용 포인트보다 더 많은 포인트 사용 요청을 하는 경우, 포인트 사용 요청이 실패한다.