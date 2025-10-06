package com.qmruan.aicodemother.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.qmruan.aicodemother.model.entity.App;
import com.qmruan.aicodemother.mapper.AppMapper;
import com.qmruan.aicodemother.service.AppService;
import org.springframework.stereotype.Service;

/**
 * 应用 服务层实现。
 *
 * @author qmruan
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{

}
