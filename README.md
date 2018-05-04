# task_balancer

Task balancer represents a pool of workers, where each worker is a thread that has its own queue. The Delegator is a main worker that delegate tasks to pool of workers. 

# decentralized_task_balancer

There is no any main workers. Each worker try to steal tasks from others. So all tasks are allocated between all workers
