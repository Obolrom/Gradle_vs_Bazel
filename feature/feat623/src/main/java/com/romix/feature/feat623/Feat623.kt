package com.romix.feature.feat623

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat623Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat623UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat623FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat623UserSummary
)

data class Feat623UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat623NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat623Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat623Config = Feat623Config()
) {

    fun loadSnapshot(userId: Long): Feat623NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat623NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat623UserSummary {
        return Feat623UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat623FeedItem> {
        val result = java.util.ArrayList<Feat623FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat623FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat623UiMapper {

    fun mapToUi(model: List<Feat623FeedItem>): Feat623UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat623UiModel(
            header = UiText("Feat623 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat623UiModel =
        Feat623UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat623UiModel =
        Feat623UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat623UiModel =
        Feat623UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat623Service(
    private val repository: Feat623Repository,
    private val uiMapper: Feat623UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat623UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat623UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat623UserItem1(val user: CoreUser, val label: String)
data class Feat623UserItem2(val user: CoreUser, val label: String)
data class Feat623UserItem3(val user: CoreUser, val label: String)
data class Feat623UserItem4(val user: CoreUser, val label: String)
data class Feat623UserItem5(val user: CoreUser, val label: String)
data class Feat623UserItem6(val user: CoreUser, val label: String)
data class Feat623UserItem7(val user: CoreUser, val label: String)
data class Feat623UserItem8(val user: CoreUser, val label: String)
data class Feat623UserItem9(val user: CoreUser, val label: String)
data class Feat623UserItem10(val user: CoreUser, val label: String)

data class Feat623StateBlock1(val state: Feat623UiModel, val checksum: Int)
data class Feat623StateBlock2(val state: Feat623UiModel, val checksum: Int)
data class Feat623StateBlock3(val state: Feat623UiModel, val checksum: Int)
data class Feat623StateBlock4(val state: Feat623UiModel, val checksum: Int)
data class Feat623StateBlock5(val state: Feat623UiModel, val checksum: Int)
data class Feat623StateBlock6(val state: Feat623UiModel, val checksum: Int)
data class Feat623StateBlock7(val state: Feat623UiModel, val checksum: Int)
data class Feat623StateBlock8(val state: Feat623UiModel, val checksum: Int)
data class Feat623StateBlock9(val state: Feat623UiModel, val checksum: Int)
data class Feat623StateBlock10(val state: Feat623UiModel, val checksum: Int)

fun buildFeat623UserItem(user: CoreUser, index: Int): Feat623UserItem1 {
    return Feat623UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat623StateBlock(model: Feat623UiModel): Feat623StateBlock1 {
    return Feat623StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat623UserSummary> {
    val list = java.util.ArrayList<Feat623UserSummary>(users.size)
    for (user in users) {
        list += Feat623UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat623UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat623UiModel {
    val summaries = (0 until count).map {
        Feat623UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat623UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat623UiModel> {
    val models = java.util.ArrayList<Feat623UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat623AnalyticsEvent1(val name: String, val value: String)
data class Feat623AnalyticsEvent2(val name: String, val value: String)
data class Feat623AnalyticsEvent3(val name: String, val value: String)
data class Feat623AnalyticsEvent4(val name: String, val value: String)
data class Feat623AnalyticsEvent5(val name: String, val value: String)
data class Feat623AnalyticsEvent6(val name: String, val value: String)
data class Feat623AnalyticsEvent7(val name: String, val value: String)
data class Feat623AnalyticsEvent8(val name: String, val value: String)
data class Feat623AnalyticsEvent9(val name: String, val value: String)
data class Feat623AnalyticsEvent10(val name: String, val value: String)

fun logFeat623Event1(event: Feat623AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat623Event2(event: Feat623AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat623Event3(event: Feat623AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat623Event4(event: Feat623AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat623Event5(event: Feat623AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat623Event6(event: Feat623AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat623Event7(event: Feat623AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat623Event8(event: Feat623AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat623Event9(event: Feat623AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat623Event10(event: Feat623AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat623Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat623Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat623Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat623Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat623Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat623Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat623Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat623Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat623Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat623Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat623(u: CoreUser): Feat623Projection1 =
    Feat623Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat623Projection1> {
    val list = java.util.ArrayList<Feat623Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat623(u)
    }
    return list
}
