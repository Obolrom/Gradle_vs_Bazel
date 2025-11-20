package com.romix.feature.feat134

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat134Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat134UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat134FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat134UserSummary
)

data class Feat134UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat134NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat134Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat134Config = Feat134Config()
) {

    fun loadSnapshot(userId: Long): Feat134NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat134NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat134UserSummary {
        return Feat134UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat134FeedItem> {
        val result = java.util.ArrayList<Feat134FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat134FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat134UiMapper {

    fun mapToUi(model: List<Feat134FeedItem>): Feat134UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat134UiModel(
            header = UiText("Feat134 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat134UiModel =
        Feat134UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat134UiModel =
        Feat134UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat134UiModel =
        Feat134UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat134Service(
    private val repository: Feat134Repository,
    private val uiMapper: Feat134UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat134UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat134UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat134UserItem1(val user: CoreUser, val label: String)
data class Feat134UserItem2(val user: CoreUser, val label: String)
data class Feat134UserItem3(val user: CoreUser, val label: String)
data class Feat134UserItem4(val user: CoreUser, val label: String)
data class Feat134UserItem5(val user: CoreUser, val label: String)
data class Feat134UserItem6(val user: CoreUser, val label: String)
data class Feat134UserItem7(val user: CoreUser, val label: String)
data class Feat134UserItem8(val user: CoreUser, val label: String)
data class Feat134UserItem9(val user: CoreUser, val label: String)
data class Feat134UserItem10(val user: CoreUser, val label: String)

data class Feat134StateBlock1(val state: Feat134UiModel, val checksum: Int)
data class Feat134StateBlock2(val state: Feat134UiModel, val checksum: Int)
data class Feat134StateBlock3(val state: Feat134UiModel, val checksum: Int)
data class Feat134StateBlock4(val state: Feat134UiModel, val checksum: Int)
data class Feat134StateBlock5(val state: Feat134UiModel, val checksum: Int)
data class Feat134StateBlock6(val state: Feat134UiModel, val checksum: Int)
data class Feat134StateBlock7(val state: Feat134UiModel, val checksum: Int)
data class Feat134StateBlock8(val state: Feat134UiModel, val checksum: Int)
data class Feat134StateBlock9(val state: Feat134UiModel, val checksum: Int)
data class Feat134StateBlock10(val state: Feat134UiModel, val checksum: Int)

fun buildFeat134UserItem(user: CoreUser, index: Int): Feat134UserItem1 {
    return Feat134UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat134StateBlock(model: Feat134UiModel): Feat134StateBlock1 {
    return Feat134StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat134UserSummary> {
    val list = java.util.ArrayList<Feat134UserSummary>(users.size)
    for (user in users) {
        list += Feat134UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat134UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat134UiModel {
    val summaries = (0 until count).map {
        Feat134UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat134UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat134UiModel> {
    val models = java.util.ArrayList<Feat134UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat134AnalyticsEvent1(val name: String, val value: String)
data class Feat134AnalyticsEvent2(val name: String, val value: String)
data class Feat134AnalyticsEvent3(val name: String, val value: String)
data class Feat134AnalyticsEvent4(val name: String, val value: String)
data class Feat134AnalyticsEvent5(val name: String, val value: String)
data class Feat134AnalyticsEvent6(val name: String, val value: String)
data class Feat134AnalyticsEvent7(val name: String, val value: String)
data class Feat134AnalyticsEvent8(val name: String, val value: String)
data class Feat134AnalyticsEvent9(val name: String, val value: String)
data class Feat134AnalyticsEvent10(val name: String, val value: String)

fun logFeat134Event1(event: Feat134AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat134Event2(event: Feat134AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat134Event3(event: Feat134AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat134Event4(event: Feat134AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat134Event5(event: Feat134AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat134Event6(event: Feat134AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat134Event7(event: Feat134AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat134Event8(event: Feat134AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat134Event9(event: Feat134AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat134Event10(event: Feat134AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat134Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat134Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat134Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat134Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat134Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat134Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat134Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat134Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat134Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat134Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat134(u: CoreUser): Feat134Projection1 =
    Feat134Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat134Projection1> {
    val list = java.util.ArrayList<Feat134Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat134(u)
    }
    return list
}
