package com.romix.feature.feat463

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat463Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat463UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat463FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat463UserSummary
)

data class Feat463UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat463NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat463Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat463Config = Feat463Config()
) {

    fun loadSnapshot(userId: Long): Feat463NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat463NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat463UserSummary {
        return Feat463UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat463FeedItem> {
        val result = java.util.ArrayList<Feat463FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat463FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat463UiMapper {

    fun mapToUi(model: List<Feat463FeedItem>): Feat463UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat463UiModel(
            header = UiText("Feat463 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat463UiModel =
        Feat463UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat463UiModel =
        Feat463UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat463UiModel =
        Feat463UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat463Service(
    private val repository: Feat463Repository,
    private val uiMapper: Feat463UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat463UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat463UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat463UserItem1(val user: CoreUser, val label: String)
data class Feat463UserItem2(val user: CoreUser, val label: String)
data class Feat463UserItem3(val user: CoreUser, val label: String)
data class Feat463UserItem4(val user: CoreUser, val label: String)
data class Feat463UserItem5(val user: CoreUser, val label: String)
data class Feat463UserItem6(val user: CoreUser, val label: String)
data class Feat463UserItem7(val user: CoreUser, val label: String)
data class Feat463UserItem8(val user: CoreUser, val label: String)
data class Feat463UserItem9(val user: CoreUser, val label: String)
data class Feat463UserItem10(val user: CoreUser, val label: String)

data class Feat463StateBlock1(val state: Feat463UiModel, val checksum: Int)
data class Feat463StateBlock2(val state: Feat463UiModel, val checksum: Int)
data class Feat463StateBlock3(val state: Feat463UiModel, val checksum: Int)
data class Feat463StateBlock4(val state: Feat463UiModel, val checksum: Int)
data class Feat463StateBlock5(val state: Feat463UiModel, val checksum: Int)
data class Feat463StateBlock6(val state: Feat463UiModel, val checksum: Int)
data class Feat463StateBlock7(val state: Feat463UiModel, val checksum: Int)
data class Feat463StateBlock8(val state: Feat463UiModel, val checksum: Int)
data class Feat463StateBlock9(val state: Feat463UiModel, val checksum: Int)
data class Feat463StateBlock10(val state: Feat463UiModel, val checksum: Int)

fun buildFeat463UserItem(user: CoreUser, index: Int): Feat463UserItem1 {
    return Feat463UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat463StateBlock(model: Feat463UiModel): Feat463StateBlock1 {
    return Feat463StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat463UserSummary> {
    val list = java.util.ArrayList<Feat463UserSummary>(users.size)
    for (user in users) {
        list += Feat463UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat463UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat463UiModel {
    val summaries = (0 until count).map {
        Feat463UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat463UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat463UiModel> {
    val models = java.util.ArrayList<Feat463UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat463AnalyticsEvent1(val name: String, val value: String)
data class Feat463AnalyticsEvent2(val name: String, val value: String)
data class Feat463AnalyticsEvent3(val name: String, val value: String)
data class Feat463AnalyticsEvent4(val name: String, val value: String)
data class Feat463AnalyticsEvent5(val name: String, val value: String)
data class Feat463AnalyticsEvent6(val name: String, val value: String)
data class Feat463AnalyticsEvent7(val name: String, val value: String)
data class Feat463AnalyticsEvent8(val name: String, val value: String)
data class Feat463AnalyticsEvent9(val name: String, val value: String)
data class Feat463AnalyticsEvent10(val name: String, val value: String)

fun logFeat463Event1(event: Feat463AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat463Event2(event: Feat463AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat463Event3(event: Feat463AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat463Event4(event: Feat463AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat463Event5(event: Feat463AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat463Event6(event: Feat463AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat463Event7(event: Feat463AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat463Event8(event: Feat463AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat463Event9(event: Feat463AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat463Event10(event: Feat463AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat463Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat463Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat463Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat463Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat463Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat463Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat463Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat463Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat463Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat463Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat463(u: CoreUser): Feat463Projection1 =
    Feat463Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat463Projection1> {
    val list = java.util.ArrayList<Feat463Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat463(u)
    }
    return list
}
