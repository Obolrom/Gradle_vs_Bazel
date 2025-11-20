package com.romix.feature.feat28

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat28Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat28UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat28FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat28UserSummary
)

data class Feat28UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat28NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat28Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat28Config = Feat28Config()
) {

    fun loadSnapshot(userId: Long): Feat28NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat28NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat28UserSummary {
        return Feat28UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat28FeedItem> {
        val result = java.util.ArrayList<Feat28FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat28FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat28UiMapper {

    fun mapToUi(model: List<Feat28FeedItem>): Feat28UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat28UiModel(
            header = UiText("Feat28 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat28UiModel =
        Feat28UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat28UiModel =
        Feat28UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat28UiModel =
        Feat28UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat28Service(
    private val repository: Feat28Repository,
    private val uiMapper: Feat28UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat28UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat28UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat28UserItem1(val user: CoreUser, val label: String)
data class Feat28UserItem2(val user: CoreUser, val label: String)
data class Feat28UserItem3(val user: CoreUser, val label: String)
data class Feat28UserItem4(val user: CoreUser, val label: String)
data class Feat28UserItem5(val user: CoreUser, val label: String)
data class Feat28UserItem6(val user: CoreUser, val label: String)
data class Feat28UserItem7(val user: CoreUser, val label: String)
data class Feat28UserItem8(val user: CoreUser, val label: String)
data class Feat28UserItem9(val user: CoreUser, val label: String)
data class Feat28UserItem10(val user: CoreUser, val label: String)

data class Feat28StateBlock1(val state: Feat28UiModel, val checksum: Int)
data class Feat28StateBlock2(val state: Feat28UiModel, val checksum: Int)
data class Feat28StateBlock3(val state: Feat28UiModel, val checksum: Int)
data class Feat28StateBlock4(val state: Feat28UiModel, val checksum: Int)
data class Feat28StateBlock5(val state: Feat28UiModel, val checksum: Int)
data class Feat28StateBlock6(val state: Feat28UiModel, val checksum: Int)
data class Feat28StateBlock7(val state: Feat28UiModel, val checksum: Int)
data class Feat28StateBlock8(val state: Feat28UiModel, val checksum: Int)
data class Feat28StateBlock9(val state: Feat28UiModel, val checksum: Int)
data class Feat28StateBlock10(val state: Feat28UiModel, val checksum: Int)

fun buildFeat28UserItem(user: CoreUser, index: Int): Feat28UserItem1 {
    return Feat28UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat28StateBlock(model: Feat28UiModel): Feat28StateBlock1 {
    return Feat28StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat28UserSummary> {
    val list = java.util.ArrayList<Feat28UserSummary>(users.size)
    for (user in users) {
        list += Feat28UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat28UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
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

fun createLargeUiModel(count: Int): Feat28UiModel {
    val summaries = (0 until count).map {
        Feat28UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat28UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
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
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat28UiModel> {
    val models = java.util.ArrayList<Feat28UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat28AnalyticsEvent1(val name: String, val value: String)
data class Feat28AnalyticsEvent2(val name: String, val value: String)
data class Feat28AnalyticsEvent3(val name: String, val value: String)
data class Feat28AnalyticsEvent4(val name: String, val value: String)
data class Feat28AnalyticsEvent5(val name: String, val value: String)
data class Feat28AnalyticsEvent6(val name: String, val value: String)
data class Feat28AnalyticsEvent7(val name: String, val value: String)
data class Feat28AnalyticsEvent8(val name: String, val value: String)
data class Feat28AnalyticsEvent9(val name: String, val value: String)
data class Feat28AnalyticsEvent10(val name: String, val value: String)

fun logFeat28Event1(event: Feat28AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat28Event2(event: Feat28AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat28Event3(event: Feat28AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat28Event4(event: Feat28AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat28Event5(event: Feat28AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat28Event6(event: Feat28AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat28Event7(event: Feat28AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat28Event8(event: Feat28AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat28Event9(event: Feat28AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat28Event10(event: Feat28AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat28Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat28Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat28Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat28Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat28Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat28Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat28Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat28Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat28Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat28Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat28(u: CoreUser): Feat28Projection1 =
    Feat28Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat28Projection1> {
    val list = java.util.ArrayList<Feat28Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat28(u)
    }
    return list
}
