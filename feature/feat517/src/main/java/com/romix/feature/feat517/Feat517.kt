package com.romix.feature.feat517

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat517Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat517UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat517FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat517UserSummary
)

data class Feat517UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat517NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat517Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat517Config = Feat517Config()
) {

    fun loadSnapshot(userId: Long): Feat517NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat517NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat517UserSummary {
        return Feat517UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat517FeedItem> {
        val result = java.util.ArrayList<Feat517FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat517FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat517UiMapper {

    fun mapToUi(model: List<Feat517FeedItem>): Feat517UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat517UiModel(
            header = UiText("Feat517 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat517UiModel =
        Feat517UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat517UiModel =
        Feat517UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat517UiModel =
        Feat517UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat517Service(
    private val repository: Feat517Repository,
    private val uiMapper: Feat517UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat517UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat517UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat517UserItem1(val user: CoreUser, val label: String)
data class Feat517UserItem2(val user: CoreUser, val label: String)
data class Feat517UserItem3(val user: CoreUser, val label: String)
data class Feat517UserItem4(val user: CoreUser, val label: String)
data class Feat517UserItem5(val user: CoreUser, val label: String)
data class Feat517UserItem6(val user: CoreUser, val label: String)
data class Feat517UserItem7(val user: CoreUser, val label: String)
data class Feat517UserItem8(val user: CoreUser, val label: String)
data class Feat517UserItem9(val user: CoreUser, val label: String)
data class Feat517UserItem10(val user: CoreUser, val label: String)

data class Feat517StateBlock1(val state: Feat517UiModel, val checksum: Int)
data class Feat517StateBlock2(val state: Feat517UiModel, val checksum: Int)
data class Feat517StateBlock3(val state: Feat517UiModel, val checksum: Int)
data class Feat517StateBlock4(val state: Feat517UiModel, val checksum: Int)
data class Feat517StateBlock5(val state: Feat517UiModel, val checksum: Int)
data class Feat517StateBlock6(val state: Feat517UiModel, val checksum: Int)
data class Feat517StateBlock7(val state: Feat517UiModel, val checksum: Int)
data class Feat517StateBlock8(val state: Feat517UiModel, val checksum: Int)
data class Feat517StateBlock9(val state: Feat517UiModel, val checksum: Int)
data class Feat517StateBlock10(val state: Feat517UiModel, val checksum: Int)

fun buildFeat517UserItem(user: CoreUser, index: Int): Feat517UserItem1 {
    return Feat517UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat517StateBlock(model: Feat517UiModel): Feat517StateBlock1 {
    return Feat517StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat517UserSummary> {
    val list = java.util.ArrayList<Feat517UserSummary>(users.size)
    for (user in users) {
        list += Feat517UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat517UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat517UiModel {
    val summaries = (0 until count).map {
        Feat517UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat517UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat517UiModel> {
    val models = java.util.ArrayList<Feat517UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat517AnalyticsEvent1(val name: String, val value: String)
data class Feat517AnalyticsEvent2(val name: String, val value: String)
data class Feat517AnalyticsEvent3(val name: String, val value: String)
data class Feat517AnalyticsEvent4(val name: String, val value: String)
data class Feat517AnalyticsEvent5(val name: String, val value: String)
data class Feat517AnalyticsEvent6(val name: String, val value: String)
data class Feat517AnalyticsEvent7(val name: String, val value: String)
data class Feat517AnalyticsEvent8(val name: String, val value: String)
data class Feat517AnalyticsEvent9(val name: String, val value: String)
data class Feat517AnalyticsEvent10(val name: String, val value: String)

fun logFeat517Event1(event: Feat517AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat517Event2(event: Feat517AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat517Event3(event: Feat517AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat517Event4(event: Feat517AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat517Event5(event: Feat517AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat517Event6(event: Feat517AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat517Event7(event: Feat517AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat517Event8(event: Feat517AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat517Event9(event: Feat517AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat517Event10(event: Feat517AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat517Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat517Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat517Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat517Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat517Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat517Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat517Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat517Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat517Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat517Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat517(u: CoreUser): Feat517Projection1 =
    Feat517Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat517Projection1> {
    val list = java.util.ArrayList<Feat517Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat517(u)
    }
    return list
}
