package io.choerodon.test.manager.infra.repository.impl;

import io.choerodon.core.domain.PageInfo;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.domain.repository.TestCycleCaseStepRepository;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseStepDO;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseStepMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseStepRepositoryImpl implements TestCycleCaseStepRepository {
	@Autowired
	TestCycleCaseStepMapper testCycleCaseStepMapper;

	@Override
	public TestCycleCaseStepE insert(TestCycleCaseStepE testCycleCaseStepE) {
		Assert.notNull(testCycleCaseStepE,"error.test.cycle.step.insert.param.not.null");

		TestCycleCaseStepDO convert = ConvertHelper.convert(testCycleCaseStepE, TestCycleCaseStepDO.class);
		if (testCycleCaseStepMapper.insert(convert) != 1) {
			throw new CommonException("error.testStepCase.insert");
		}
		return ConvertHelper.convert(convert, TestCycleCaseStepE.class);
	}

	@Override
	public void delete(TestCycleCaseStepE testCycleCaseStepE) {
		Assert.notNull(testCycleCaseStepE,"error.test.cycle.step.delete.param.not.null");
		TestCycleCaseStepDO convert = ConvertHelper.convert(testCycleCaseStepE, TestCycleCaseStepDO.class);
		testCycleCaseStepMapper.delete(convert);
	}

	@Override
	public TestCycleCaseStepE update(TestCycleCaseStepE testCycleCaseStepE) {
		Assert.notNull(testCycleCaseStepE,"error.test.cycle.step.update.param.not.null");

		TestCycleCaseStepDO convert = ConvertHelper.convert(testCycleCaseStepE, TestCycleCaseStepDO.class);
		if (testCycleCaseStepMapper.updateByPrimaryKeySelective(convert) != 1) {
			throw new CommonException("error.testStepCase.update");
		}
		return ConvertHelper.convert(testCycleCaseStepMapper.selectByPrimaryKey(convert.getExecuteStepId()), TestCycleCaseStepE.class);
	}

	@Override
	public Page<TestCycleCaseStepE> query(TestCycleCaseStepE testCycleCaseStepE, PageRequest pageRequest) {
		if(!(testCycleCaseStepE !=null && testCycleCaseStepE.getExecuteId()!=null)){
			throw new CommonException("error.test.cycle.case.step.caseId.not.null");
		}
		TestCycleCaseStepDO convert = ConvertHelper.convert(testCycleCaseStepE, TestCycleCaseStepDO.class);

		List<TestCycleCaseStepDO> dto = testCycleCaseStepMapper.queryWithTestCaseStep(convert, pageRequest.getPage() * pageRequest.getSize(), pageRequest.getSize());
		Long total= 0L;
		if(dto!=null && !dto.isEmpty()){
			total=testCycleCaseStepMapper.queryWithTestCaseStep_count(testCycleCaseStepE.getExecuteId());
		}
		PageInfo info = new PageInfo(pageRequest.getPage(), pageRequest.getSize());
		Page<TestCycleCaseStepDO> page = new Page<>(Optional.ofNullable(dto).orElseGet(ArrayList::new), info, total);

		return ConvertPageHelper.convertPage(page, TestCycleCaseStepE.class);
	}

	public List<TestCycleCaseStepE> query(TestCycleCaseStepE testCycleCaseStepE) {
		TestCycleCaseStepDO convert = ConvertHelper.convert(testCycleCaseStepE, TestCycleCaseStepDO.class);
		List<TestCycleCaseStepDO> dto = testCycleCaseStepMapper.select(convert);
		return ConvertHelper.convertList(dto, TestCycleCaseStepE.class);
	}

	/**
	 * @deprecated not be used
	 */
	@Override
	@Deprecated
	public TestCycleCaseStepE queryOne(TestCycleCaseStepE testCycleCaseStepE) {
		TestCycleCaseStepDO convert = ConvertHelper.convert(testCycleCaseStepE, TestCycleCaseStepDO.class);

		TestCycleCaseStepDO dto = testCycleCaseStepMapper.selectOne(convert);

		return ConvertHelper.convert(dto, TestCycleCaseStepE.class);
	}

	@Override
	public List<TestCycleCaseStepE> queryCycleCaseForReporter(Long[] ids) {
		Assert.notNull(ids,"error.queryCycleCaseForReporter.ids.not.null");
		ids = Stream.of(ids).filter(Objects::nonNull).toArray(Long[]::new);
		return ConvertHelper.convertList(testCycleCaseStepMapper.queryCycleCaseForReporter(ids), TestCycleCaseStepE.class);
	}

}
