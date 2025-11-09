package com.romix.feature.feat1

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat1Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat1UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat1FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat1UserSummary
)

data class Feat1UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat1NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat1Repository(
    private val api: FakeApiService = FakeApiService(FakeNetworkClient()),
    private val config: Feat1Config = Feat1Config()
) {

    fun loadSnapshot(userId: Long): Feat1NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat1NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat1UserSummary {
        return Feat1UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat1FeedItem> {
        val result = ArrayList<Feat1FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat1FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat1UiMapper {

    fun mapToUi(model: List<Feat1FeedItem>): Feat1UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat1UiModel(
            header = UiText("Feat1 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat1UiModel =
        Feat1UiModel(
            header = UiText("No data"),
            items = emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat1UiModel =
        Feat1UiModel(
            header = UiText("Loading..."),
            items = emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat1UiModel =
        Feat1UiModel(
            header = UiText("Error"),
            items = emptyList(),
            loading = false,
            error = message
        )
}

class Feat1Service(
    private val repository: Feat1Repository,
    private val uiMapper: Feat1UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat1UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat1UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat1UserItem1(val user: CoreUser, val label: String)
data class Feat1UserItem2(val user: CoreUser, val label: String)
data class Feat1UserItem3(val user: CoreUser, val label: String)
data class Feat1UserItem4(val user: CoreUser, val label: String)
data class Feat1UserItem5(val user: CoreUser, val label: String)
data class Feat1UserItem6(val user: CoreUser, val label: String)
data class Feat1UserItem7(val user: CoreUser, val label: String)
data class Feat1UserItem8(val user: CoreUser, val label: String)
data class Feat1UserItem9(val user: CoreUser, val label: String)
data class Feat1UserItem10(val user: CoreUser, val label: String)

data class Feat1StateBlock1(val state: Feat1UiModel, val checksum: Int)
data class Feat1StateBlock2(val state: Feat1UiModel, val checksum: Int)
data class Feat1StateBlock3(val state: Feat1UiModel, val checksum: Int)
data class Feat1StateBlock4(val state: Feat1UiModel, val checksum: Int)
data class Feat1StateBlock5(val state: Feat1UiModel, val checksum: Int)
data class Feat1StateBlock6(val state: Feat1UiModel, val checksum: Int)
data class Feat1StateBlock7(val state: Feat1UiModel, val checksum: Int)
data class Feat1StateBlock8(val state: Feat1UiModel, val checksum: Int)
data class Feat1StateBlock9(val state: Feat1UiModel, val checksum: Int)
data class Feat1StateBlock10(val state: Feat1UiModel, val checksum: Int)

fun buildFeat1UserItem(user: CoreUser, index: Int): Feat1UserItem1 {
    return Feat1UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat1StateBlock(model: Feat1UiModel): Feat1StateBlock1 {
    return Feat1StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat1UserSummary> {
    val list = ArrayList<Feat1UserSummary>(users.size)
    for (user in users) {
        list += Feat1UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat1UserSummary>): List<UiListItem> {
    val items = ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat1UiModel {
    val summaries = (0 until count).map {
        Feat1UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat1UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat1UiModel> {
    val models = ArrayList<Feat1UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat1AnalyticsEvent1(val name: String, val value: String)
data class Feat1AnalyticsEvent2(val name: String, val value: String)
data class Feat1AnalyticsEvent3(val name: String, val value: String)
data class Feat1AnalyticsEvent4(val name: String, val value: String)
data class Feat1AnalyticsEvent5(val name: String, val value: String)
data class Feat1AnalyticsEvent6(val name: String, val value: String)
data class Feat1AnalyticsEvent7(val name: String, val value: String)
data class Feat1AnalyticsEvent8(val name: String, val value: String)
data class Feat1AnalyticsEvent9(val name: String, val value: String)
data class Feat1AnalyticsEvent10(val name: String, val value: String)

fun logFeat1Event1(event: Feat1AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat1Event2(event: Feat1AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat1Event3(event: Feat1AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat1Event4(event: Feat1AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat1Event5(event: Feat1AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat1Event6(event: Feat1AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat1Event7(event: Feat1AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat1Event8(event: Feat1AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat1Event9(event: Feat1AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat1Event10(event: Feat1AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat1Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat1Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat1Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat1Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat1Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat1Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat1Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat1Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat1Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat1Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat1(u: CoreUser): Feat1Projection1 =
    Feat1Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat1Projection1> {
    val list = ArrayList<Feat1Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat1(u)
    }
    return list
}
