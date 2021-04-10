pragma solidity >=0.4.21 <0.7.5;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

contract TutorialToken is ERC20  {
    uint256 public INITIAL_SUPPLY = 12000;
    constructor() public ERC20("Tutorial", "TT") {
        _mint(msg.sender, INITIAL_SUPPLY);
    }

}