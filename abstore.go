/*
 * 
 * 文件名称 : main.go 
 * 创建者 : linwf
 * 创建日期: 2018/08/23 
 * 文件描述: 主入口函数
 * 历史记录: 无 
 */

package main

import (
	"fmt"
	"strconv"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

// SimpleChaincode implements a simple chaincode to manage an asset
type SimpleChaincode struct {
}

var id int = 0

// 链码实例化时，调用Init函数初始化数据
// 链码升级时，也会调用此函数重置或迁移数据
func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("ex02 Init")
	// 获取交易提案中的参数
	_, args := stub.GetFunctionAndParameters()
	if len(args) != 0 {
		return shim.Error("Incorrect arguments. Expecting a key and a value")
	}

	return shim.Success(nil)
}

// 调用Invoke函数进行资产交易
// 每笔交易通过get或set操作Init函数创建的key和value
// 通过set可以创建新的key和value
func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	// 获取交易提案中的函数和参数
	fn, args := stub.GetFunctionAndParameters()

	var result string
	var err error
	if fn == "set" {
		result, err = set(stub, args)
	} else { // assume 'get' even if fn is nil
		result, err = get(stub, args)
	}
	if err != nil {
		return shim.Error(err.Error())
	}

	// Return the result as success payload
	return shim.Success([]byte(result))
}

// 保存key和value到账本上
// 如果key存在，覆盖原有的value
func set(stub shim.ChaincodeStubInterface, args []string) (string, error) {
	if len(args) != 1 {
		return "", fmt.Errorf("Incorrect arguments. Expecting a value")
	}
	id = id + 1
	err := stub.PutState(strconv.Itoa(id), []byte(args[0]))
	if err != nil {
		return "", fmt.Errorf("Failed to set asset: %s", args[0])
	}
	return args[0], nil
}

// 获取key对应的value
func get(stub shim.ChaincodeStubInterface, args []string) (string, error) {
	if len(args) != 1 {
		return "", fmt.Errorf("Incorrect arguments. Expecting a key")
	}

	value, err := stub.GetState(args[0])
	if err != nil {
		return "", fmt.Errorf("Failed to get asset: %s with error: %s", args[0], err)
	}
	if value == nil {
		return "", fmt.Errorf("Asset not found: %s", args[0])
	}
	return string(value), nil
}

func main() {
	err := shim.Start(new(SimpleChaincode))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}
