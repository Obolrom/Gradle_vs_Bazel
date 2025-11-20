package com.romix.feature.feat555

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat555Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat555UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat555FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat555UserSummary
)

data class Feat555UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat555NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat555Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat555Config = Feat555Config()
) {

    fun loadSnapshot(userId: Long): Feat555NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat555NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat555UserSummary {
        return Feat555UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat555FeedItem> {
        val result = java.util.ArrayList<Feat555FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat555FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat555UiMapper {

    fun mapToUi(model: List<Feat555FeedItem>): Feat555UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat555UiModel(
            header = UiText("Feat555 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat555UiModel =
        Feat555UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat555UiModel =
        Feat555UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat555UiModel =
        Feat555UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat555Service(
    private val repository: Feat555Repository,
    private val uiMapper: Feat555UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat555UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat555UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat555UserItem1(val user: CoreUser, val label: String)
data class Feat555UserItem2(val user: CoreUser, val label: String)
data class Feat555UserItem3(val user: CoreUser, val label: String)
data class Feat555UserItem4(val user: CoreUser, val label: String)
data class Feat555UserItem5(val user: CoreUser, val label: String)
data class Feat555UserItem6(val user: CoreUser, val label: String)
data class Feat555UserItem7(val user: CoreUser, val label: String)
data class Feat555UserItem8(val user: CoreUser, val label: String)
data class Feat555UserItem9(val user: CoreUser, val label: String)
data class Feat555UserItem10(val user: CoreUser, val label: String)

data class Feat555StateBlock1(val state: Feat555UiModel, val checksum: Int)
data class Feat555StateBlock2(val state: Feat555UiModel, val checksum: Int)
data class Feat555StateBlock3(val state: Feat555UiModel, val checksum: Int)
data class Feat555StateBlock4(val state: Feat555UiModel, val checksum: Int)
data class Feat555StateBlock5(val state: Feat555UiModel, val checksum: Int)
data class Feat555StateBlock6(val state: Feat555UiModel, val checksum: Int)
data class Feat555StateBlock7(val state: Feat555UiModel, val checksum: Int)
data class Feat555StateBlock8(val state: Feat555UiModel, val checksum: Int)
data class Feat555StateBlock9(val state: Feat555UiModel, val checksum: Int)
data class Feat555StateBlock10(val state: Feat555UiModel, val checksum: Int)

fun buildFeat555UserItem(user: CoreUser, index: Int): Feat555UserItem1 {
    return Feat555UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat555StateBlock(model: Feat555UiModel): Feat555StateBlock1 {
    return Feat555StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat555UserSummary> {
    val list = java.util.ArrayList<Feat555UserSummary>(users.size)
    for (user in users) {
        list += Feat555UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat555UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat555UiModel {
    val summaries = (0 until count).map {
        Feat555UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat555UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat555UiModel> {
    val models = java.util.ArrayList<Feat555UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat555AnalyticsEvent1(val name: String, val value: String)
data class Feat555AnalyticsEvent2(val name: String, val value: String)
data class Feat555AnalyticsEvent3(val name: String, val value: String)
data class Feat555AnalyticsEvent4(val name: String, val value: String)
data class Feat555AnalyticsEvent5(val name: String, val value: String)
data class Feat555AnalyticsEvent6(val name: String, val value: String)
data class Feat555AnalyticsEvent7(val name: String, val value: String)
data class Feat555AnalyticsEvent8(val name: String, val value: String)
data class Feat555AnalyticsEvent9(val name: String, val value: String)
data class Feat555AnalyticsEvent10(val name: String, val value: String)

fun logFeat555Event1(event: Feat555AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat555Event2(event: Feat555AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat555Event3(event: Feat555AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat555Event4(event: Feat555AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat555Event5(event: Feat555AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat555Event6(event: Feat555AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat555Event7(event: Feat555AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat555Event8(event: Feat555AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat555Event9(event: Feat555AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat555Event10(event: Feat555AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat555Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat555Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat555Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat555Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat555Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat555Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat555Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat555Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat555Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat555Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat555(u: CoreUser): Feat555Projection1 =
    Feat555Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat555Projection1> {
    val list = java.util.ArrayList<Feat555Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat555(u)
    }
    return list
}
