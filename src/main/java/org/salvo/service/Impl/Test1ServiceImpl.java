package org.salvo.service.Impl;

import org.salvo.model.Test1;
import org.salvo.service.Test1Service;
import org.salvo.service.base.Impl.BaseService;
import org.springframework.stereotype.Service;

@Service("test1Service")
public class Test1ServiceImpl extends BaseService<Test1> implements Test1Service {

}
